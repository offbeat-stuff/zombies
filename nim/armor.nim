var f = 0.1087f
import random

var s = [0, 0, 0, 0, 0, 0]

for i in 0 .. 100000:
    var r = rand(0..2)
    for j in 1 .. 3:
        if rand(1.0) < f: r.inc()
    s[r].inc()

echo s
# result: [23599, 32192, 33350, 9677, 1149, 34]
