### ¿Cómo realizar un commit?

tpype: subject

- Types:

 - feat: Una nueva caracteristica.

 - fix: Se soluciono un bug.

 - docs: Se realizaron cambios en la documentacion.

 - style: Se aplico formato, comas y puntos faltantes, etc; Sin cambios en el codigo.

 - refactor: Refactorizacion del codigo en produccion.

 - test: Se añadieron pruebas, refactorizacion de pruebas; Sin cambios en el codigo.

 - chore: Actualizacion de tareas de build, configuracion del admin. de paquetes; Sin cambios en el codigo.

- Subject:

 - El asunto no debe contener mas de 50 caracteres, debe iniciar con una letra mayuscula y no terminar con un punto. Debemos ser imperativos al momento de redactar nuestro commit, es decir hay que ser objetivos

- Optionals:

 - Body: Usarlo solo en caso de que el commit sea complejo y requiera un descripción mas detallada. Utilizamos el cuerpo para explicar el ¿Que y Porque? de un commit y no el ¿Como?

 - Footer: El pie es opcional al igual que el cuerpo, pero este es usado para el seguimiento de los IDs con incidencias.

### ¿Cómo realizar un PR?

- Siempre se deberá commitear y pushear a una branch especifica para el feature en cuestión.
- El nombre de la branch deberá cumplir con este formato: type-*feature resumido en un minimo de 4 palabras*
- Si todos los tests pasan, crear un PR contra master, comentar el #issueArreglado y una breve descripción de lo hecho, seleccionar reviewers, esperar una aprobación.
- Comentario:
 - Descripción:
   La descripción.

   Incluir imagen si se toca la interfaz sería ideal.

   Fixes: #(issue)


