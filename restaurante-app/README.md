# Sistema de Gestion de Restaurante - Proyecto base (Punto 12)

Estructura generada segun el punto 12 del avance ("Proyecto base con la estructura,
codigo y clases segun entidades"), basada en las entidades del punto 11:
Producto, Mesa, Pedido, DetallePedido, Usuario, ReporteVenta.

Paquete principal: com.sv.grupo3.restaurante

## Estructura
```
com.sv.grupo3.restaurante
 |- model       (Producto, Mesa, Pedido, DetallePedido, Usuario, ReporteVenta)
 |- dao         (interfaces CRUD por entidad)
 |- repository  (implementacion en memoria de los DAO - HashMap)
 |- service     (logica de negocio: HU-001 a HU-004)
 |- util        (GeneradorId)
 |- exception   (RecursoNoEncontradoException)
 |- main        (Main.java - demo funcional)
 |- test        (ProductoServiceTest.java - pruebas manuales)
```

## Como ejecutar en VSCode
1. Instala la extension "Extension Pack for Java" (Microsoft) y ten un JDK 17+ instalado.
2. Abre esta carpeta (`restaurante-app`) en VSCode: File > Open Folder.
3. VSCode detectara automaticamente el codigo fuente en `src/`.
4. Abre `src/com/sv/grupo3/restaurante/main/Main.java`.
5. Haz clic en "Run" (boton que aparece arriba del metodo `main`) o presiona F5.

## Como ejecutar por terminal (sin VSCode)
```bash
cd restaurante-app
mkdir -p bin
find src -name "*.java" > sources.txt
javac -d bin -encoding UTF-8 @sources.txt
java -cp bin com.sv.grupo3.restaurante.main.Main
```

Para correr las pruebas manuales:
```bash
java -ea -cp bin com.sv.grupo3.restaurante.test.ProductoServiceTest
```
(el flag `-ea` habilita los `assert`)

## Siguiente iteracion recomendada
- Reemplazar los repositorios en memoria (HashMap) por una implementacion JDBC
  contra MySQL/PostgreSQL, manteniendo las mismas interfaces DAO (asi el resto
  del codigo no cambia).
- Agregar el modulo de ReporteVenta (HU-001 parte de reportes) con filtros por
  rango de fechas y calculo del platillo mas vendido.
- Anadir Mesa y su relacion real con Pedido (foreign key) en vez de solo copiar
  numero/zona.
- Sustituir las pruebas manuales por JUnit 5 una vez se agregue Maven/Gradle.
