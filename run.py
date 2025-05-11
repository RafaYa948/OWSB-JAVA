import os, subprocess, sys

SRC = "src"
BIN = "bin"
MAIN = "LoginPage"

files = [os.path.join(d, f) for d, _, fs in os.walk(SRC) for f in fs if f.endswith(".java")]
os.makedirs(BIN, exist_ok=True)
if subprocess.call(["javac", "-d", BIN] + files) != 0:
    sys.exit(1)
subprocess.call(["java", "-cp", BIN, MAIN])
