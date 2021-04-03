#tests loops and procedures 
java main/Main -march=c55x -dsim call1 c55x/test/loop_procedure_global.dis
java main/Main -march=c55x -dsim call2 c55x/test/simple_loop3.dis
java main/Main -march=c55x -dsim array1 c55x/test/array1.dis

#fir function (that gives compiled mac's)
java main/Main -march=c55x -dsim mac c55x/test/mac.dis

#tests 16 bit signed compared values, use short in java (shorts are signed in java)
java main/Main -march=c55x -dsim qsort c55x/test/quicksort.dis

# write start in simulation prompt
# tests that calls with globals work
# should return 0x8e in t0

#tests 16 bit signed compared values, use short in java (shorts are signed in java)
#java main/Main -march=c55x -dsim qsort c55x/test/quicksort2.dis

