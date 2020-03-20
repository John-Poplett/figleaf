# Figleaf
[![Clojars Project](https://img.shields.io/clojars/v/figleaf.svg)](https://clojars.org/figleaf)

Figleaf is a lightweight code coverage utility for Clojure
that layers on top of the clojure.test unit test framework.

You specify a namespace to instrument and Figleaf instruments
all the *public* functions in that namespace. As your unit tests execute,
it keeps a real-time tally of how many times your test suite invokes
each function was invoked. At the end of a test run, you get a report
of which functions were tested and which were not.

Despite its lightweight, as its name suggests, it's sufficient
to ahem... cover your private parts. Though it doesn't analyze coverage by
statement or condition / decision logic, it's surprisingly useful.
It's also easy to use.

Figleaf is based on and structured similarly to [cl-figleaf](https://github.com/John-Poplett/cl-figleaf), an earlier
implementation for Common Lisp.

## Figleaf @ work

For clj-based command line development, add an alias to deps.edn, specifying
your two class files: the first specifying the class "under test" and the second
a class file containing corresponding unit tests:

```clojure
:aliases     {:figleaf   {:extra-deps  {org.clojure/clojure {:mvn/version "1.10.1"}
                                        figleaf/figleaf     {:mvn/version "1.0.1"}}
                          :extra-paths ["test"]
                          :main-opts   ["-m" "figleaf.core" "sentiment.core" "sentiment.core_test"]}}
```
To run Figleaf from the command line, invoke the figleaf alias:

> clj -A figleaf

Here is an example of figleaf output on a sentiment analyzer project:

```bash
MacBook Pro:sentiment john$ clj -Afigleaf
20-03-20 13:00:06 MacBook Pro INFO [sentiment.core:79] - Building AFFIN map for scoring words...

Testing sentiment.core_test
20-03-20 13:00:06 MacBook Pro DEBUG [sentiment.core:85] - Generating tokens...
20-03-20 13:00:06 MacBook Pro INFO [sentiment.core:79] - Building AFFIN map for scoring words...

Ran 7 tests containing 11 assertions.
0 failures, 0 errors.
FUNCTIONS TESTED: #'sentiment.core/confusion-matrix, #'sentiment.core/compute-valid-values, #'sentiment.core/build-AFFIN-map, #'sentiment.core/->Sample, #'sentiment.core/tokenize-text, #'sentiment.core/mean, #'sentiment.core/classification-report, #'sentiment.core/zip, #'sentiment.core/handle-sentiment-data, #'sentiment.core/process-sentiment-data
FUNCTIONS UNTESTED: #'sentiment.core/make-predictions, #'sentiment.core/map->Sample, #'sentiment.core/-main, #'sentiment.core/AFFIN-predict
CODE COVERAGE: Functions 14, Tested 10, Ratio 71%
```

## Figleaf @ play
Figleaf plays well with the REPL. 

From the REPL:
```clojure
(use 'figleaf.core)
(require 'namespace-under-test)
(require 'unit-test-namespace)
(run-tests namespace-under-test unit-test-namespace)
```

Here's an example run of invoking Figleaf from the REPL prompt:
```clojure
MacBook Pro:sentiment john$ clj
Clojure 1.10.1
user=> (use 'figleaf.core)
nil
user=> (require 'sentiment.core)
20-03-20 22:05:19 MacBook Pro INFO [sentiment.core:79] - Building AFFIN map for scoring words...
nil
user=> (require 'sentiment.core_test)
nil
user=> (run-tests sentiment.core sentiment.core_test)
Testing sentiment.core_test
20-03-20 22:06:11 MacBook Pro DEBUG [sentiment.core:85] - Generating tokens...
20-03-20 22:06:11 MacBook Pro INFO [sentiment.core:79] - Building AFFIN map for scoring words...

Ran 7 tests containing 11 assertions.
0 failures, 0 errors.
FUNCTIONS TESTED: #'sentiment.core/confusion-matrix, #'sentiment.core/compute-valid-values, #'sentiment.core/build-AFFIN-map, #'sentiment.core/->Sample, #'sentiment.core/tokenize-text, #'sentiment.core/mean, #'sentiment.core/classification-report, #'sentiment.core/zip, #'sentiment.core/handle-sentiment-data, #'sentiment.core/process-sentiment-data
FUNCTIONS UNTESTED: #'sentiment.core/make-predictions, #'sentiment.core/map->Sample, #'sentiment.core/-main, #'sentiment.core/AFFIN-predict
CODE COVERAGE: Functions 14, Tested 10, Ratio 71%
nil
```

Figleaf keeps statistics after a run in transaction-friendly "let-over-lambda" style closure.
A handful of functions are defined to access its statistics: These include the functions:

* all
* tested
* untested
* namespace-function-count
* funcall-count



After running tests, we can query the counter from the REPL prompt as this example
demonstrates:
```clojure
user=> (all)
("#'sentiment.core/process-sentiment-data" "#'sentiment.core/build-AFFIN-map" "#'sentiment.core/mean" "#'sentiment.core/-main" "#'sentiment.core/make-predictions" "#'sentiment.core/classification-report" "#'sentiment.core/AFFIN-predict" "#'sentiment.core/->Sample" "#'sentiment.core/handle-sentiment-data" "#'sentiment.core/zip" "#'sentiment.core/compute-valid-values" "#'sentiment.core/confusion-matrix" "#'sentiment.core/tokenize-text" "#'sentiment.core/map->Sample")
user=> (funcall-count)
1015
user=> (tested)
("#'sentiment.core/confusion-matrix" "#'sentiment.core/compute-valid-values" "#'sentiment.core/build-AFFIN-map" "#'sentiment.core/->Sample" "#'sentiment.core/tokenize-text" "#'sentiment.core/mean" "#'sentiment.core/classification-report" "#'sentiment.core/zip" "#'sentiment.core/handle-sentiment-data" "#'sentiment.core/process-sentiment-data")
user=> (/ (funcall-count) (count (tested)))
203/2
user=> (/ (funcall-count) (count (tested)) 1.0)
101.5
user=> (tested-function-count)
10
user=> (/ (funcall-count) (count (all)) 1.0)
72.5
```
## Benefits
> Nothing measured, nothing gained.
  
As the adage suggests, writing unit tests alone doesn't promote
progress. Uneven test coverage is conceivably as useless as no
coverage at all.

As soon as you start measuring test coverage, it is surprising how
fast you adjust your coding practice. It provides a new source of 
satisfaction. Whereas before, you might have taken some pride in the
sheer number of lines of code you were cranking out, now you have the
counter-balancing satisfaction of knowing how well your code is tested.

Your behaviors change. As you drive to have 100% coverage, you will
do things that never occurred to you before! You will start to prune
and preen to eliminate unused functions or replace "hand-rolled" functions
with equivalents from highly-regarded open source projects.

You discover that one way to 100% coverage is to reduce the test burden.

Code coverage promotes pace. As long as you are pursuing complete
coverage, your code output will not increase to the point that
you're writing unqualified code.

## Coding with a Lisp
A long time ago, I had been intrigued by the way object-relational modeling
middleware companies performed bytecode rewriting just
to instrument Java methods. Talk about pluck!

I also had first-hand experience with C/C++ code coverage tools. Again those tools go
to extraordinary lengths—parsing and rewriting source code—to achieve the desired result.

I was motivated to write the original Common Lisp Figleaf to find out how much easier it might be
to write than any comparable system designed for a static, procedural language. I was not disappointed.

The ease of the original implementation and the port from Common Lisp to Clojure was straight-forward; the
effort, not extraordinary.

Figleaf is a shining example of the benefits of Lisp and a language where 
code is data. I cringe to think of the effort involved to produce like functionality
in a static procedural language. The final result is much more satisfying. 
Consider these differences:
 
### Static Instrumentation
* Instrument at compile time
* Requires special builds
* Requires intimate, semantic knowledge of sometimes parser-unfriendly
languages and the skill level of a compiler writer

### Dynamic Instrumentation
* Instrument at run-time or class-load time
* Special build not required
* Instrument live images
* Remove instrumentation from live images
* Instrumentation simple for Clojure and other languages where functions are first-class objects

## Copyright and License
Copyright (C) 2012-2020 John H. Poplett. All rights reserved.

Distributed under the Eclipse Public License, the same as Clojure.
