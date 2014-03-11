Played for fun with free-lock algorithms and benchmarking (inspired by Concurrency in Practice book)
Also I've added STM(akka) version. ~~It is slower comparing to LockFree implementation
But still about 2,5 times faster than blocking implementation.~~

WRONG!!! Computing fibonacci numbers up to 100.000 invocations

Impl/Threads      | 1      | 2      | 4      | 8      | 16     |
---               | ---    | ---    | ---    | ---    | ---    |
IntrinsicLocking  | 4959   | 3132   | 3458   | 3059   | 3402   |
ExplicitLocking   | 4112   | 5348   | 6478   | 12895  | 13492  |
STM               | 5193   | 5210   | 4899   | 5259   | 6733   |
LockFree          | 4362   | 3601   | 3660   | 4732   | 4923   |

Test results using JMH
##

@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 200, batchSize = 20000)
@Measurement(iterations = 200, batchSize = 20000)
@Fork(3)
@State(Scope.Benchmark)

Threads  | Batch Size |
---      | ---        |
 1       | 20000      |
 2       | 20000      |
 4       | 20000      |
 8       | 20000      |

Impl/Threads (ms) | 1      | 2      | 4      | 8      |
---               | ---    | ---    | ---    | ---    |
ExplicitLocking   | 12     | 67     | 193    | 512    |
IntrinsicLocking  | 12     | 52     | 176    | 604    |
LockFree          | 12     | 44     | 144    | 636    |
STM               | 25     | 63     | 191    | 880    |


1 thread
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       600       12.283        0.222       ms
f.b.JmhBench.intrinsicLock       ss       600       12.176        0.191       ms
f.b.JmhBench.lockFree            ss       600       12.845        0.199       ms
f.b.JmhBench.stm                 ss       600       25.093        0.235       ms


2 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       600       67.178        1.094       ms
f.b.JmhBench.intrinsicLock       ss       600       52.704        0.987       ms
f.b.JmhBench.lockFree            ss       600       44.218        0.600       ms
f.b.JmhBench.stm                 ss       600       63.779        0.861       ms

4 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       600      193.588        2.558       ms
f.b.JmhBench.intrinsicLock       ss       600      176.956        2.967       ms
f.b.JmhBench.lockFree            ss       600      144.788        3.294       ms
f.b.JmhBench.stm                 ss       600      191.152        3.967       ms

8 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       600      512.533        6.792       ms
f.b.JmhBench.intrinsicLock       ss       600      604.254        8.899       ms
f.b.JmhBench.lockFree            ss       600      636.976       17.633       ms
f.b.JmhBench.stm                 ss       600      880.862       23.738       ms

---------


@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 100, batchSize = JmhBench.BATCH_SIZE)
@Measurement(iterations = 100, batchSize = JmhBench.BATCH_SIZE)
@Fork(3)
@State(Scope.Benchmark)

Threads  | Batch Size |
---      | ---        |
 1       | 160000     |
 2       | 80000      |
 4       | 40000      |
 8       | 20000      |


Impl/Threads (ms) | 1      | 2      | 4      | 8      |
---               | ---    | ---    | ---    | ---    |
ExplicitLocking   | 625    | 676    | 593    | 512    |
IntrinsicLocking  | 626    | 671    | 659    | 604    |
LockFree          | 665    | 671    | 624    | 636    |
STM               | 749    | 696    | 718    | 880    |

1 thread
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       300      625.648        2.068       ms
f.b.JmhBench.intrinsicLock       ss       300      626.289        1.871       ms
f.b.JmhBench.lockFree            ss       300      665.112        3.574       ms
f.b.JmhBench.stm                 ss       300      749.284        3.074       ms

2 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       300      676.973       17.829       ms
f.b.JmhBench.intrinsicLock       ss       300      671.252       10.743       ms
f.b.JmhBench.lockFree            ss       300      671.302        4.092       ms
f.b.JmhBench.stm                 ss       300      696.592        8.888       ms

4 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       300      593.641       13.221       ms
f.b.JmhBench.intrinsicLock       ss       300      659.666       14.298       ms
f.b.JmhBench.lockFree            ss       300      624.277       11.940       ms
f.b.JmhBench.stm                 ss       300      718.004       13.362       ms

8 threads
Benchmark                      Mode   Samples         Mean   Mean error    Units
f.b.JmhBench.explicitLock        ss       600      512.533        6.792       ms
f.b.JmhBench.intrinsicLock       ss       600      604.254        8.899       ms
f.b.JmhBench.lockFree            ss       600      636.976       17.633       ms
f.b.JmhBench.stm                 ss       600      880.862       23.738       ms

