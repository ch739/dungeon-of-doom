README

COMPILING

To compile the source, please use the following command in the directory containing src and bin.

javac -d bin -cp src src/dod/*.java



RUNNING

In the bin sub-directory run:
java dod/Program


However if you wish to run a particular map run:
java dod/Program [map filename]

Place the map file you wish to use in the "res" folder.

EXAMPLE:
java dod/Program map1.txt


JOIN GAME

To join game you must first send a HELLO + name command.