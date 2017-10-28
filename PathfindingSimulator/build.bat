mkdir out 2>nul
javac .\CSVParsing\CSVParsing.java .\MatrixGenerator\MatrixGenerator.java .\GenericCode\Generic.java .\Simulator\Simulator.java
javac -d .\out .\Test.java
