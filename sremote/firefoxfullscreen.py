import xaut
import sys
import time

k = xaut.keyboard()
k.click_delay(10)

w = xaut.window.find_window("Firefox")
w.activate()
w.wait_active()
#select the search box
k.click(95)
w.minimize()
