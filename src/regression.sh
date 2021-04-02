#!/bin/sh
# regression.sh MUST BE RUN BEFORE ANY SOURCE COMMIT.
# IF IT DOES NOT PRINT OK YOU HAVE BROKEN SOMETHING.
# EITHER FIX YOUR CODE OR, IF YOUR CODE IS NOT BROKEN,
# FIX THE REGRESSION. HOWEVER NEVER COMMIT ANYTHING
# THAT MAKES THE REGRESSION FAIL.
#

fail() {
    echo "FAIL"
    exit 1
}

rm -f output.dot
java main/Main -E -march=c55x -Ob c55x/test/cfg.dis $@ || fail
# kps edited c55x/test/cfg.dis to make sure this works:
diff -ub c55x/test/cfg.dis emitdefaultdir/cfg.dis || fail
diff -u c55x/test/cfg.bbg.dot output.dot || fail
#cp output.dot c55x/test/cfg.bbg.dot

rm -f output.dot
java main/Main -E -march=c55x -Oc c55x/test/cfg.dis $@ || fail
diff -u c55x/test/cfg.cfg.dot output.dot || fail
#cp output.dot c55x/test/cfg.cfg.dot

#
# Correct CFGs for rc and test ARM programs.
#
rm -f output.dot
java main/Main -march=arm7 -Ob test/rc4onefile.s $@ || fail
diff -u test/rc4onefile.bbg.dot output.dot || fail
#cp output.dot test/rc4onefile.bbg.dot

rm -f output.dot
java main/Main -march=arm7 -Oc test/rc4onefile.s $@ || fail
diff -u test/rc4onefile.cfg.dot output.dot || fail
#cp output.dot test/rc4onefile.cfg.dot


echo "OK"
