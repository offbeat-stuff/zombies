var f = 0.095f
import random

var s = [0, 0, 0, 0, 0, 0]

for i in 0 .. 100000:
    var r = rand(1)
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    if (rand(1.0) < f): r.inc()
    s[r].inc()

echo s
# result: [33435, 47460, 16505, 2443, 154, 4]
