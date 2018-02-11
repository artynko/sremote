import xaut
import sys

title = sys.argv[1]
w = xaut.window.find_window(title)
w.minimize()
w.wait_active()

