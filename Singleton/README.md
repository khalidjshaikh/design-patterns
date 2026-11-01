# Singleton Pattern (Java)

This module demonstrates common implementations of the Singleton design pattern.

## Variants

- `EagerSingleton` — instance created at class load; thread-safe, possibly wasteful.
- `LazySingleton` — created on first use; not thread-safe.
- `SynchronizedSingleton` — thread-safe via synchronized method.
- `DoubleCheckedSingleton` — thread-safe double-checked locking (volatile field).
- `BillPughSingleton` — initialization-on-demand holder idiom (recommended default).
- `EnumSingleton` — enum-based; safest against serialization/reflection (Effective Java).

## Scripts

```bash
./scripts/build.sh
./scripts/test.sh
./scripts/run.sh
```
