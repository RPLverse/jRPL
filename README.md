# jRPL
An RPL to bytecode compiler

**jRPL** è un compilatore di un subset minimale del linguaggio **RPL** (Reverse Polish Lisp, linguaggio delle vecchie calcolatrici programmabili HP48) a **bytecode**, realizzato in Java utilizzando la libreria [ASM](https://asm.ow2.io/).

Il progetto nasce come esercizio per l’esame di *Software Development Methods* (Laurea Magistrale in Computer Engineering), ma è progettato con un’architettura estendibile in vista di possibili sviluppi futuri.

### Build
```bash
./gradlew clean build -x test
./gradlew test --warning-mode all
./gradlew :run --args="examples/demo.rpl --out-dir build/gen-classes --class-name org.jrpl.gen.Demo"
./gradlew javadoc

```

### Esecuzione demo
```bash
java -cp build/gen-classes:build/classes/java/main org.jrpl.gen.Demo 2 4

```
