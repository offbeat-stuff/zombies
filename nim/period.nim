import strutils

var buffer = ""
var spaces = 0
var className = "Period"
var values = "GRACE,EASY,HARD,NIGHTMARE"
var valuesSize = values.split(',').len()

var intMembers = [
    ("spawnTries", [0, 1, 2, 5, 25]),
    ("enchantLevel", [0, 0, 5, 20, 40])
]

var doubleMembers = [
    ("commonEquipment", [0.0, 0.0, 0.05, 0.1, 0.5]),
    ("rareEquipment", [0.0, 0.0, 0.0, 0.05, 0.1]),
    ("shieldChance", [0.0, 0.0, 0.0, 0.05, 0.1])
]

var boolMembers = [
    ("treasure", [false, false, true, true])
]

proc addLine(s: string) =
    buffer &= repeat(' ', spaces) & s & "\n"

template tabbed(body) =
    spaces += 2
    body
    spaces -= 2

proc addIntValues(name: string, v: string) =
    assert (v.split(',').len - 1) == valuesSize
    addLine "public static List<Integer> " & name & " = List.of(" & v & ");"
    addLine ""
    addLine "public int get" & capitalizeAscii(name) & " (double progress) {"
    tabbed:
        addLine "int index = this.ordinal();"
        addLine "return (int)MathHelper.lerp(progress," & name &
                ".get(index)," & name & ".get(index + 1));"
    addLine "}"

proc addDoubleValues(name: string, v: string) =
    assert (v.split(',').len - 1) == valuesSize
    addLine "public static List<Double> " & name & " = List.of(" & v & ");"
    addLine ""
    addLine "public double get" & capitalizeAscii(name) & " (double progress) {"
    tabbed:
        addLine "int index = this.ordinal();"
        addLine "return (double)MathHelper.lerp(progress," & name &
                ".get(index)," & name & ".get(index + 1));"
    addLine "}"

proc addBoolValues(name: string, v: string) =
    assert v.split(',').len == valuesSize
    addLine "public static List<Boolean> " & name & " = List.of(" & v & ");"
    addLine ""
    addLine "public boolean get" & capitalizeAscii(name) & " () {"
    tabbed:
        addLine "return " & name & ".get(this.ordinal());"
    addLine "}"

addLine "package org.codeberg.zenxarch.zombies.difficulty;"
addLine ""
addLine "import java.util.List;"
addLine "import net.minecraft.util.math.MathHelper;"
addLine ""
addLine "public enum " & className & " {"
tabbed:
    addLine split(values, ',').join(",\n  ") & ";"
    for (name, value) in intMembers:
        addLine ""
        addIntValues name, strip($value, chars = {'[', ']'})
    for (name, value) in doubleMembers:
        addLine ""
        addDoubleValues name, strip($value, chars = {'[', ']'})
    for (name, value) in boolMembers:
        addLine ""
        addBoolValues name, strip($value, chars = {'[', ']'})
addLine "}"

echo buffer
