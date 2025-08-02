var f = 0.095f
import random

var s = [0, 0, 0, 0, 0, 0]

for i in 0 .. 10000:
    var r = rand(2)
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    s[r].inc()

echo s
# result: [2286, 3143, 3335, 1065, 159, 13]
