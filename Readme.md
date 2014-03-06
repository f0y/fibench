Played for fun with free-lock algorithms and benchmarking (inspired by Concurrency in Practice book)
Also I've added STM(akka) version. It is slower comparing to LockFree implementation
But still about 2,5 times faster than blocking implementation.

Computing fibonacci numbers up to 100.000 invocations

Impl/Threads      | 1      | 2      | 4      | 8      | 16     |
---               | ---    | ---    | ---    | ---    | ---    |
IntrinsicLocking  | 4959   | 3132   | 3458   | 3059   | 3402   |
ExplicitLocking   | 4112   | 5348   | 6478   | 12895  | 13492  |
STM               | 5193   | 5210   | 4899   | 5259   | 6733   |
LockFree          | 4362   | 3601   | 3660   | 4732   | 4923   |