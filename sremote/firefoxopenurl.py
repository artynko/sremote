import xaut
import sys
import time

k = xaut.keyboard()
k.click_delay(10)

url = sys.argv[1]
w = xaut.window.find_window("Firefox")
w.activate()
w.wait_active()
#select the search box
k.down(37)
k.click(46)
k.up(37)
k.type(url)
m = xaut.mouse()
m.move_delay(1)
m.move(1920, 1000)

