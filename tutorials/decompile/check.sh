#!/usr/bin/env bash
# Verifica lo que el tutorial decompile (README.md) dice que genera kotlinc, con javap, CFR y @Metadata.
# Compila en una carpeta temporal. CFR se baja una vez de Maven Central a ~/.cache/kotlin-for-java-devs/.
#
#   tutorials/decompile/check.sh    (con kotlinc 2.4.x en el PATH: `sdk env` en la raíz del repo)
set -euo pipefail

here="$(cd "$(dirname "$0")" && pwd)"
ex="$here/examples"
work="$(mktemp -d)"
trap 'rm -rf "$work"' EXIT
pkg="dev/raul/oskar/learning/kotlin/tutorials/decompile"
fqn="dev.raul.oskar.learning.kotlin.tutorials.decompile"

pass=0
ok()   { pass=$((pass + 1)); echo "  ✓ $1"; }
fail() { echo "  ✗ $1"; echo "    esperado: $2"; echo "    obtenido: $3"; exit 1; }
# has <descripción> <texto> <salida>: la salida contiene el texto
has()    { [[ "$3" == *"$2"* ]] && ok "$1" || fail "$1" "$2" "$(echo "$3" | head -20)"; }
# hasnt <descripción> <texto> <salida>: la salida NO contiene el texto
hasnt()  { [[ "$3" != *"$2"* ]] && ok "$1" || fail "$1" "que no aparezca: $2" "$(echo "$3" | grep -F -- "$2" | head -5)"; }
# method <archivo .class> <nombre>: el bytecode de un método (javap -c -p), desde su firma hasta la línea en blanco
method() { javap -c -p "$1" | awk -v m="$2" 'index($0, " " m "(") && /\(/ {p=1} p {print} p && /^$/ {exit}'; }

command -v kotlinc >/dev/null || { echo "kotlinc no está en el PATH (en la raíz del repo: sdk env)"; exit 1; }
version="$(kotlinc -version 2>&1)"; echo "$version"
[[ "$version" == *"kotlinc-jvm 2.4."* ]] || { echo "Este tutorial está escrito para Kotlin 2.4.x (sdk env en la raíz)"; exit 1; }

kotlinc "$ex"/*.kt -jvm-target 21 -d "$work/t21" 2>/dev/null
kotlinc "$ex"/*.kt -d "$work/t8" 2>/dev/null
c21="$work/t21/$pkg"; c8="$work/t8/$pkg"

echo "1. Clases"
out="$(javap -p "$c21/Account.class")"
has "data class: component1, copy y copy\$default" "copy\$default(" "$out"
has "data class: constructor sintético para los valores por defecto" "kotlin.jvm.internal.DefaultConstructorMarker" "$out"
has "data class: equals, hashCode y toString" "public boolean equals(java.lang.Object);" "$out"
out="$(javap -p "$c21/Position.class")"
has "lateinit: el campo queda public" "public java.lang.String lastTrade;" "$out"
has "propiedad calculada: solo el getter isEmpty()" "public final boolean isEmpty();" "$out"
hasnt "propiedad calculada: sin campo isEmpty" "boolean isEmpty;" "$out"
has "setter privado: no hay setQuantity público" "public final int getQuantity();" "$out"
hasnt "setter privado: setQuantity no aparece" "setQuantity" "$out"
has "companion: campo estático Companion" "public static final $fqn.Position\$Companion Companion;" "$out"
has "const: public static final en la clase externa" "public static final int MAX_QUANTITY;" "$out"
has "val del companion: campo privado estático en la clase externa" "private static final java.lang.String DEFAULT_TICKER;" "$out"
has "val del companion: accesor sintético" "access\$getDEFAULT_TICKER\$cp" "$out"
has "@JvmStatic: método estático en Position" "public static final $fqn.Position empty(java.lang.String);" "$out"
has "@JvmStatic: el getter sigue en Companion" "getDEFAULT_TICKER" "$(javap -p "$c21/Position\$Companion.class")"
has "object: campo INSTANCE" "public static final $fqn.Market INSTANCE;" "$(javap -p "$c21/Market.class")"
has "lateinit: el getter lanza si no se inicializó" "throwUninitializedPropertyAccessException" "$(method "$c21/Position.class" getLastTrade)"

echo "2. Funciones"
out="$(javap -p "$c21/Functions.class")"
has "@file:JvmName: la fachada se llama Functions" "public final class $fqn.Functions" "$out"
has "parámetro por defecto: fee\$default con máscara" "fee\$default(java.math.BigDecimal, java.math.BigDecimal, int, java.lang.Object)" "$out"
has "@JvmOverloads: sobrecarga de un parámetro" "feeForJava(java.math.BigDecimal);" "$out"
has "extensión: estático con el receptor como primer parámetro" "public static final boolean isTicker(java.lang.String);" "$out"
has "parámetro no nullable: checkNotNullParameter" "Intrinsics.checkNotNullParameter" "$(method "$c21/Functions.class" describe)"
has "plantilla con target 21: makeConcatWithConstants" "makeConcatWithConstants" "$(method "$c21/Functions.class" describe)"
has "plantilla con target 1.8: StringBuilder" "java/lang/StringBuilder" "$(method "$c8/Functions.class" describe)"
hasnt "lambda dentro de filter (inline): sin invokedynamic" "invokedynamic" "$(method "$c21/Functions.class" pricesAbove)"
has "lambda que escapa: invokedynamic a Function1" "InvokeDynamic #1:invoke:(D)Lkotlin/jvm/functions/Function1;" "$(method "$c21/Functions.class" above)"
has "lambda que escapa con target 1.8: también invokedynamic" "invokedynamic" "$(method "$c8/Functions.class" above)"
out="$(method "$c21/Functions.class" total)"
hasnt "inline: total no invoca a measure" "measure" "$out"
has "inline: el cuerpo de measure quedó copiado (nanoTime)" "java/lang/System.nanoTime" "$out"
has "reified: instanceof String en el llamador" "instanceof    #" "$(method "$c21/Functions.class" countStrings | grep 'class java/lang/String')"

echo "3. Tipos"
out="$(javap -p "$c21/TypesKt.class")"
has "value class: desaparece de la firma y el nombre se mutila" "public static final long addCents-XyPO2wg(long, long);" "$out"
has "suspend: parámetro Continuation y retorno Object" "java.lang.Object fetchPrice(java.lang.String, kotlin.coroutines.Continuation<? super java.lang.Double>);" "$out"
has "value class: box-impl y constructor-impl" "box-impl(long)" "$(javap -p "$c21/Cents.class")"
has "when sobre sealed con target 21: typeSwitch" "typeSwitch" "$(method "$c21/TypesKt.class" kind)"
has "when sobre sealed con target 1.8: instanceof" "instanceof" "$(method "$c8/TypesKt.class" kind)"
has "when exhaustivo: NoWhenBranchMatchedException" "NoWhenBranchMatchedException" "$(method "$c21/TypesKt.class" kind)"
has "sealed con target 21: PermittedSubclasses" "PermittedSubclasses" "$(javap -v "$c21/Order.class")"
hasnt "sealed con target 1.8: sin PermittedSubclasses" "PermittedSubclasses" "$(javap -v "$c8/Order.class")"
has "bytecode: target 21 = major 65" "major version: 65" "$(javap -v "$c21/Market.class")"

echo "4. CFR"
cache="${XDG_CACHE_HOME:-$HOME/.cache}/kotlin-for-java-devs"; cfr="$cache/cfr-0.152.jar"
mkdir -p "$cache"
[[ -f "$cfr" ]] || curl -sfL -o "$cfr" https://repo1.maven.org/maven2/org/benf/cfr/0.152/cfr-0.152.jar
out="$(java -jar "$cfr" "$c21/Market.class" 2>/dev/null)"
has "CFR: el object como Java" "public static final Market INSTANCE = new Market();" "$out"
has "CFR: @Metadata con mv 2.4.0 y k=1" "@Metadata(mv={2, 4, 0}, k=1" "$out"
has "CFR: el when como switch de Java 21" "SwitchBootstraps.typeSwitch" "$(java -jar "$cfr" "$c21/TypesKt.class" 2>/dev/null)"
has "CFR: fee\$default con la máscara de bits" "if ((n & 2) != 0)" "$(java -jar "$cfr" "$c21/Functions.class" 2>/dev/null)"

echo "5. @Metadata"
out="$(kotlin "$ex/metadata.main.kts" "$work/t21" "$fqn.Account" "$fqn.Cents" "$fqn.TypesKt" 2>&1)"
has "metadata: Account es data class" "kind=CLASS  data=true  value=false" "$out"
has "metadata: Cents es value class" "data=false  value=true" "$out"
has "metadata: el nombre Kotlin de la función mutilada" "fun addCents  ->  JVM addCents-XyPO2wg(JJ)J" "$out"
has "metadata: suspend se distingue" "suspend fun fetchPrice" "$out"
has "metadata: TypesKt es una fachada de archivo (k=2)" "$fqn.TypesKt  (k=2" "$out"

echo
echo "Todo en orden: $pass verificaciones."
