# clean-addr

A samll utility to split danish address column (string) into smaller
components columns (street name, no, floor and door).

Examples:

Address                      | Street           | No   | Floor | Door
-----------------------------|------------------|------|-------|-----
Store Trillegade 117 A, 2.TH | Store Trillegade | 117a | 2     | th
Store Trillegade  117A,  2TH | Store Trillegade | 117a | 2     | th
Store Trillegade  117A 2.TH. | Store Trillegade | 117a | 2     | th
Store Trillegade 117A 2,TH   | Store Trillegade | 117a | 2     | th

## Usage

Split address inside csv file using:

    lein run infile.csv 5 outfile.csv

The outfile is optional. If left out the outfile name will be based on
the infile postpended by `_split` ie. `infile_split.csv`

## License

Copyright © 2014

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
