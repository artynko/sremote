import xaut
import time
from subprocess import Popen, PIPE

k = xaut.keyboard()
k.click_delay(20)
#k.down(64)
#k.click(23)
#k.up(64)

w = xaut.window.find_window("Firefox")
w.activate()
w.wait_active()
# select the search box
k.click(95)
k.down(37)
k.click(46)
k.up(37)
k.type("https://www.youtube.com/tv{numbersign}/watch?v=CevxZvSJLk8{Return}")
# minimize firefox
time.sleep(30)


w = xaut.window.find_window("XBMC")
time.sleep(3)
print("pre minimize")
w.minimize()
time.sleep(3)
w.activate()

cf4 = '''keydown Alt_L
key F4
keyup Alt_L
'''

def keypress(s):
	p = Popen(['xte'], stdin=PIPE)
	p.communicate(input=s)

#keypress(cf4)
