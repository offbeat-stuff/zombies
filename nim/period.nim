import strutils

var buffer = ""
var spaces = 0

proc addLine(s: string) =
    buffer &= repeat(' ', spaces) & s & "\n"

template tabbed(body) =
    spaces += 2
    body
    spaces -= 2
