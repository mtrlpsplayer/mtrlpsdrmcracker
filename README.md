# MTR Let's Play server resource pack DRM cracker

Because screw DRM, that's why.

## Compiling

``mvn clean compile assembly:single``

You need Maven. Tested with JDK 25.

## Running

``java -jar target/mtrlpsdrmcracker-1.0-SNAPSHOT-jar-with-dependencies.jar -f <file to decrypt>``

If you want to decrypt the entirety of the resourcepack, on Linux you should just be able to execute the following:
``find <path to the folder with the MTR synced resourcepack> -type f -exec java -jar <path to source folder>/target/mtrlpsdrmcracker-1.0-SNAPSHOT-jar-with-dependencies.jar -f {} \;``

If you want some resemblance of a progress indicator, add ``-print`` after ``-type f``.

## Licensing

MIT
