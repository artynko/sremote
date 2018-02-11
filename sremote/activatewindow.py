import xaut
import sys

title = sys.argv[1]
w = xaut.window.find_window(title)
w.activate()
w.wait_active()

