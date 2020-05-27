# Contribuir con Colorful Post-it
## Resumen
En este documento se explica cómo contribuir al proyecto Colorful-post-it. Se asume que se dispone de una cuenta en GitHub.com. En caso de no ser así crear una nueva cuenta. Los cambios de código propuestos se publicarán en su propio fork del proyecto
y creará un Pull Request para que se agreguen los cambios.
 
## Fork
Realizar un fork del repositorio haciendo clic en el botón 'Fork' en la parte superior derecha. Se redirigirá a su propio fork del repositorio. Copie la URL del repositorio de Git haciendo clic en el portapapeles junto a la URL en el lado derecho de la página debajo de 'Clone with HTTPS'. Pegará esta URL cuando realice el siguiente comando git clone.
Siga estos pasos para configurar un repositorio local:
 
    $ git clone https://github.com/CUENTA/s1-2020-tp-grupo3.git
    $ cd s1-2020-tp-grupo3
    $ git remote add upstream https://github.com/PracticaDS/s1-2020-tp-grupo3.git
    $ git checkout master
    $ git fetch upstream
    $ git rebase upstream/master

## Realizar cambios

Es importante que cree una nueva rama para realizar cambios y que no modifique la rama master. Realizar los cambios en una branch nueva, por ejemplo ‘feature_x’. Esta rama ‘feature_x’ se creará en su repositorio local y se enviará a su repositorio fork en GitHub. Una vez que esta rama esté en su fork, creará un Pull Request para los cambios que se agregarán al proyecto.
Se recomienda crear una nueva branch cada vez que desee contribuir al proyecto y solo realizar un seguimiento de los cambios para esa pull request en esta rama.
 
    $ git checkout -b feature_x
       (cambios)
    $ git status
    $ git add .
    $ git commit -a -m "mensaje descriptivo"
    
Rebase para incluir actualizaciones en upstream/master
Es importante que mantenga actualizada su branch en su repositorio local. Deberá hacer esto antes de comenzar a trabajar en una función, así como justo antes de enviar sus cambios como una solicitud de extracción. 

    $ git checkout master
    $ git fetch upstream
    $ git rebase upstream/master
    $ git checkout feature_x
    $ git rebase master

## Crear un Pull Request en GitHub
Cuando esté satisfecho con sus cambios y esté listo para contribuir, creará un Pull Request en GitHub. Esto se realiza mediante la inserción de los cambios locales en su repositorio fork y luego crea un Pull Request en GitHub.

    $ git push origin master
    $ git push origin feature_x
    
Crear un Pull Request:
Ir al repositorio fork: https://github.com/CUENTA/s1-2020-tp-grupo3

Click al boton 'Compare & pull request' 

Validar que el pull request va a mergear en master  y que el origen es la branch feature_x

Ingresar una descripción de los cambios realizados. Ser lo más detallado posible. Luego hacer click en 'Send pull request'

## Reportar un bug

Para hacer el reporte de un bug se debe crear un issue en el repositorio de github. Crearlo con el label ‘bug’. Incluir , de ser posible, informe de error y capturas.
