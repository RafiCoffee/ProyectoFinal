**PROYECTO FIN DE CICLO DAM DAM**

**ZapeTask**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.001.png)

**Rubén De Haro Sánchez CURSO 2023/24**

Zapetask Pag 33

**Índice**

1\.[Que es ZapeTask](#_page2_x56.70_y81.60)

2\.Análisis

- 2.1\.[Android Studio Y Kotlin](#_page4_x56.70_y81.60)

- 2.2\.[Firebase Y Bankend](#_page5_x56.70_y81.60)

- 2.3\.[Diagrama De Clase](#_page7_x56.70_y81.60)

- 2.4\.[Diagrama De Casos De Uso ](#_page9_x56.70_y81.60)

3\.Diseño

- 3.1\.[Prototipo Visual](#_page11_x56.70_y81.60)

- 3.2\.[Logos](#_page13_x56.70_y81.60)

- 3.3\.[Navegación](#_page14_x56.70_y81.60)

- 3.4\.[Arquitectura (Clean MMVV, Hilt) ](#_page16_x56.70_y81.60)

4\.[Frontend Y Caracteristicas De FireBase ](#_page22_x56.70_y81.60)

5\.[Conclusiones](#_page27_x56.70_y81.60)

6\.[Futuras Versiones](#_page28_x56.70_y81.60)

7\.[Problemas Durante El Desarrollo ](#_page29_x56.70_y81.60)

8\.[Bibliografía](#_page31_x56.70_y81.60)

<a name="_page2_x56.70_y81.60"></a>**1- Que Es ZapeTask**

ZapeTask es un proyecto que consiste en el desarrollo de una aplicación movil la cual aspira a convertirse en una especie de agenda virtual pensada para gente desorganizada o simplemente personas que quieras planificarse con la facilidad de recordar cuales son sus quehaceres con un simple 

vistazo a su teléfono movil. Esta aplicación movil esta diseñada para Android utilizando programas que facilitan esta tarea en dicha plataforma.

Para todo el backend he utilizado los servicios de Firebase ya que ofrece un servidor gratuito abierto las 24 horas del día donde almaceno todos los datos necesarios para el funcionamiento de la aplicación y me permite desarrollarla y probarla directamente en mi dispositivo físico.

ZapeTask ofrece las siguientes funciones:

- Permite  registrarte  y  loguearte  mediante  unas  pantallas dedicadas al registro e inicio de sesión respectivamente.
- Cada usuario registrado tiene un id y al iniciar sesión se le asigna  un  token  para  interactuar  correctamente  con  la aplicación.
- Antes de entrar a la aplicación se mostrara una pantalla de carga mientras se recuperan los datos del usuario del servidor.
- Una vez en la pantalla principal podemos ver 3 menús para interactuar, los cuales explicaré más adelante en profundidad.
- Dentro de la aplicación podremos crear, eliminar y gestionar tareas, buscar y agregar usuarios como amigos para ver sus 

  tareas pendientes y gestionar nuestro perfil.

<a name="_page4_x56.70_y81.60"></a>**2.1- Análisis – Android Studio Y Kotlin**

Debido a que mi aplicación se pensó en un principio para móviles, no pude evitar pensar en Android  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.002.png)Studio para comenzar a desarrollarla. Nunca había  usado este programa antes de cursar este ciclo, pero la  facilidad  que  te  da  para  desarrollar  aplicaciones  móviles de Android es innegable, por lo que no fue  difícil decantarme por este programa.

En cuanto a Kotlin, desde su inclusión en Android![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.003.png)  Studio  3.0  como  alternativa  al compilador de Java en 2017 hasta ahora, se ha convertido ya en el lenguaje favorito para los desarrolladores de apps de Android a pesar de su  breve  trayectoria. Aparte  de  que  es  el lenguaje con el que he aprendido a utilizar Android Studio, este lenguaje aborda muchas de las limitaciones y problemas de Java, posee una sintaxis muy limpia y concisa y contiene características avanzas de seguridad que ayudan a prevenir errores comunes que podría pasar por alto en otro lenguaje. Por esto y por mi ansia de conocer y dominar otros lenguajes, finalmente mi decisión fue Kotlin.

<a name="_page5_x56.70_y81.60"></a>**2.2- Análisis – Firebase Y Backend**

Para mi aplicación, ZapeTask, no he desarrollado un Backend en sí, para está tarea he utilizado Firebase. Firebase es una plataforma de desarrollo dedicada para páginas web y aplicaciones móviles. Firebase me![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.004.png)  ha  proporcionado  una  amplia  variedad  de herramientas y servicios para ayudarme a desarrollar, construir y mejorar mi aplicación. Toda mi aplicación se beneficia de esta plataforma para recuperar, gestionar, editar, crear o eliminar datos que están subidos a internet en una base de datos que me proporciona Firebase. Firebase no solo me brinda la función de tener una base de datos a la que puedo acceder fácilmente, sino muchas otras funciones muy útiles que me han hecho el desarrollo más sencillo a lo largo de los días. Aquí mencionaré las características que he utilizado de entre todas las que ofrece, son las siguientes:

- Base De Datos en Tiempo Real (Realtime Database):

Este módulo, como su propio nombre indica, es una base de datos la  cual  podemos  gestionar  desde  nuestra  aplicación  muy facilmente. Visual y semanticamente se asemeja mucho a un archivo JSON, extensión en la cual podremos exportar e importar nuestra base de datos a Firebase.

- Authenticación (Authentication):

Este modulo me ha ayudado a facilitar y automatizar la gestión de usuarios. Registrar, loguear, eliminar y generación de token son las funciones más utiles y las que he utilizado en este ámbito.

- Almacen (Storage):

En este módulo puedes mantener online cualquier tipo de archivo el cual puedes recuperar desde tu aplicación facilmente. Para lo único que he utilizado el Storage es para almacenar y recuperar imagenes, mayormente fotos de perfil de los usuarios.

Explicaré más en profundidad la implementación de estas funciones en el punto [***4- Frontend Y Características De Firebase***](#_page22_x56.70_y81.60).

<a name="_page7_x56.70_y81.60"></a>**2.3- Análisis – Diagrama De Clases**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.005.png)El diagrama contiene las siguientes entidades: Usuario (id, token, nombre, correo electrónico, contraseña, foto de perfil, idAmigo y una lista de amigos), Amigo (id del Usuario al que se refiere, idAmigo, nombre, foto y estado de solicitud de amistad), Tarea ( id, nombre, fecha, hora, tipo de tarea, recordatorio y idUsuario del Usuario al que pertenece cada tarea) y Notificación (id, tipo de Notificación [amigo o tarea], idUsuario del Usuario al que pertenece la notificación y idInfo [id de la tarea o amigo al que se refiere]). Un Usuario puede tener varios amigos que se almacenan en su lista, cada amigo se refiere a otro Usuario Además, un Usuario también  puede  crear  Tareas.  Por  último,  la  aplicación  genera Notificaciones cuando se crean Tareas o los Usuarios solicitan para ser amigos de otros usuarios. Cada Notificación pertenece a un Usuario.

<a name="_page9_x56.70_y81.60"></a>**2.4- Análisis – Diagrama De Casos De Uso**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.006.png)En este diagrama podemos ver dos actores, los cuales representan a dos usuarios. He decidido colocar dos actores en el diagrama para dejar ver que en mi aplicación dos usuarios pueden solicitar una amistad, aceptarla o incluso rechazarla. Podemos ver que un usuario puede registrarse e inmediatamente  iniciar  sesión  o,  por  el  contrario,  iniciar  sesión directamente. Tras iniciar sesión mientras se muestra la *SplashScreen* de mi aplicación se cargaran los datos del usuario. Una vez cargados el usuario será dirigido a la pantalla principal donde puede acceder a todas las funciones de la aplicación. ***Gestión de Tareas,*** los usuarios pueden crear, editar y eliminar tareas, que se almacenan en una base de datos en tiempo real gracias a Firebase; ***Notificaciones***, cada vez que se crea una tarea o se realiza una solicitud de amistad, se generan notificaciones automáticas que se muestran a los usuarios pertinentes; ***Consultas de Notificaciones,***  los  usuarios  pueden  consultar  sus  notificaciones  en cualquier momento, facilitando el seguimiento de sus tareas y solicitudes de amistad pendientes; ***Interacción Social***, los usuarios pueden ver a otros usuarios existentes y enviar solicitudes de amistad las cuales deben ser aceptadas por el usuario receptor; ***Gestión de Amigos***, una vez aceptadas las solicitudes de amistad, los usuarios pueden ver a sus amigos actuales y las tareas de sus amigos en pantallas dedicadas.

<a name="_page11_x56.70_y81.60"></a>**3.1- Diseño – Prototipo Visual**

Antes de meterme a idear y probar interfaces de usuario en Android Studio use una herramienta para crearlas a modo de prototipo de manera rápida y sencilla. Para esta tarea pensé en utilizar Adobe Xd, ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.007.png)

que es un programa muy útil para crear y probar 

interfaces muy completas rápidamente, pero finalmente me decante por un programa menos conocido y más limitado pero que cumplió su función igualmente. Este programa se trata de Balsamiq Wireframes, el cual utilicé para crear diversas versiones de lo que podría ser la interfaz, de las cuales finalmente me quedé con está.

Está imagen se trata de el primer prototipo de la pantalla principal de mi aplicación. Como se puede ver esta ya definidos las formas de desplazarse por ella como el menú de navegación inferior y el menú lateral.![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.008.png)  Gracias  a  este  trabajo previo, me fue muy fácil traspasar este trabajo a Android Studio ya que tenía una idea clara de lo que 

quería y no di ninguna vuelta innecesaria mientras diseñaba el xml.

Esta  fue  la  primera  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.009.jpeg)interfaz  que  implementé  en  Android  Studio  siguiendo mi prototipo y  no gasté más tiempo del  necesario,  lo  cual  me  ahorro mucho tiempo al  crear todas las pantallas  de mi aplicación. 

Este  proceso  es  el  que  seguí  para  las  demás  pantallas. 

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.010.jpeg)

<a name="_page13_x56.70_y81.60"></a>**3.2- Diseño – Logos**

En cuanto al logo de mi app quería crear algo  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.011.png)representativo de está. En un principio pensé  

en usar la cara del pequeño robot mascota de  mi aplicación como logo, pero el resultado  final no me gustó demasiado ya que era una  imagen demasiado “difícil” de recordar, por lo  que empecé a pensar opciones más sencillas y  minimalistas. 

Ya que el nombre de mi app se separaba en dos palabras clave Zapet (El nombre de la mascota de la app) y Task (Tareas en ingles) se me ocurrió juntar las dos primeras letras de estas palabras con una fuente recta y limpia. Este fue el resultado final:

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.012.png)

<a name="_page14_x56.70_y81.60"></a>**3.3- Diseño – Navegación**

Mi objetivo era crear una navegación sencilla y compacta para movernos por la aplicación rápido, fácil, intuitiva y eficazmente. La aplicación dispone principalmente de 3 menús: 

- Menú De Navegación Inferior:
- En este menú visualizamos 3 opciones:
- Zapet:
  - Este es el menú en el que nos encontramos por defecto.
- ZapetMedia:
  - Esta  pantalla  aparecerá  vacía  si  acabamos  de registrarnos, básicamente mostrara las próximas tareas de los usuarios que tenemos agregados como amigos.
- Tareas:
- Esta  es  la  pantalla  donde  interactuaremos  con  la función principal de la aplicación, crear y gestionar nuestras tareas. Podemos la fecha para buscar tareas especificas.
- Menú Lateral
- En este menú visualizamos 5 opciones:
- Cerrar Sesión:
  - Simplemente un botón para cerrar la sesión
- Perfil:
  - Esta opción nos llevará a una pantalla para gestionar nuestro perfil.
- Notificaciones:
  - Esta pantalla nos mostrara todas las notificaciones pendientes del usuario, solicitudes de amigo, tareas…
- Amigos:
- Aquí  veremos  todos  los  amigos  que  tenemos 

  agregados.

- Añadir Amigos:
  - En esta pantalla podremos agregar amigos entre todos los usuarios que se han registrado.
- Menú Básico:
- En este pequeño menú podremos encontrar 2 opciones:
- Código Amigo:
  - Nos mostrara nuestro código de amigo para copiarlo y compartirlo con otra persona.
- Como usar ZapeTask:
- Nos mostrara un pequeño tutorial de como usar la 

  aplicación.

<a name="_page16_x56.70_y81.60"></a>**3.4- Diseño – Arquitectura**

En cuanto a la arquitectura de mi aplicación me gustaría hablar sobre tres puntos importantes en los cuales me he apoyado a la hora del desarrollo de la misma. He utilizado una ***Clean Arquitecture*** para gestionar todos los archivos de proyecto, el modelo de diseño ***MVVM*** para gestionar los datos 

y la UI de manera mas independiente y por último ***Hilt*** para inyectar en cualquier parte de mi código servicios importantes que iba necesitando.

Mi ***Clean  Arquitecture***  esta  formada  por  3  carpetas  principales, *application*, *data*, y *ui*. ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.013.png)

En la primera carpeta simplemente tengo almacenado mi clase que hereda de aplicación, necesaria para múltiples funciones que mi app contiene. En cuanto a *data*, al desglosarla podemos ver múltiples categorías:

En está carpeta básicamente contenemos todas las clases que tengan algo que ver con el![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.014.png) manejo de datos, ya sean callbacks, interfaces, servicios… También podemos ver los modelos y  repositorios  los  cuales  usaremos  para comunicarnos  correctamente  con  nuestros respectivos *ViewModels*.

En cuanto a la carpeta de *ui*  podemos ver, como su nombre indica, todo lo relacionado  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.015.png)con  la  interfaz  de  usuario.  Adaptadores,  dialogos,  views…  Esta  última  subcarpeta  contiene todas nuestras *activities* y *fragments*.  

Podemos  ver  la  carpeta *modelView*,  que  contiene los *ViewModels*, hablemos de ellos.

Podemos  ver  varios *ViewModels*  en  la carpeta,![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.016.png)  los  cuales  he  utilizado  para sincronizar los datos con la UI a excepción de *FragmentViewModel*  el  cual  he utilizado para otra tarea la cual explicare en [***7- Problemas Durante El Desarrollo***](#_page29_x56.70_y81.60).

Los  demás *ViewModels*  si  son  utilizados  para  un  manejo  más independiente de los datos y UI. Por ejemplo, *FriendViewModel*  es utilizado en la pantalla donde se muestran los usuarios para agregar amigos. Este ViewModel en concreto se beneficia de los servicios y repositorios de Usuarios y otros servicios o atributos que he necesitado, pero generalmente cada ViewModel emplea, como es lógico, sus servicios y repositorios correspondientes, *TareasViewModel* usa *TareasRepository* y *TareasService*, *UsuarioViewModel* usa *UserRepository* y *UserService*…

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.017.png)

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.018.png)

El caso de *InfoViewModel*  es ligeramente diferente, pero lo suficiente como para poseer una mención en profundidad. Este *ViewModel* gestiona dos objetos, notificaciones y amigos. Decidí esto como una forma de ahorrar tiempo a la hora programar la función de mostrar tanto las notificaciones como los amigos en un mismo *Activity*. Básicamente este *ViewModel*  opera en *InfoActivity*, al cual podemos acceder desde dos opciones diferentes para informar al ViewModel que es lo que tiene que mostrar.

**MainActivity**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.019.png)

Desde el *MainActivity* dependiendo de la opción que el usuario elija se le pasa un parametro u otro al *InfoActivity* el cual al crearse comprueba que parametro se le ha pasado para cargar un objeto u otro en pantalla. La actividad tambien tiene en cuenta que si en caso de que no reciba ningún parametro devolverá al usuario a la pantalla anterior, ya que no sabe que objeto mostrar.

**InfoActivity**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.020.png)

Mi *InfoViewModel* al manejar dos objetos (Notificación y Amigo) necesita como parametros los repositorios de ambos. La herramienta que he utilizado para inyectar estos repositorios es ***Hilt***. He usado *Hilt* por toda mi aplicación para inyectar los servicios o repositorios que iba necesitando en cada momento de manera rápida y sencilla. Esta herramienta me ha sido muy útil a lo largo del desarrollo no solo para proporcionar los repositorios y servicios necesarios a los *ViewModels* sino para utilizar las funciones que adicionales que he ido creando en mis servicios en mis actividades. En el caso especifico de los servicios simplemente lo que he tenido que hacer es crear el propio servicio con sus funciones y definir cada uno en un objeto, *ServicesModules*. Tras esto simplemente inyecto los servicios necesarios en mis actividades con la etiqueta @Inject.

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.021.png)

Es necesario que tanto nuestra clase aplicación como nuestras actividades tengan estas etiquetas para que *Hilt* funcione correctamente e inyecte las clases que queremos.

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.022.png)

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.023.png)

<a name="_page22_x56.70_y81.60"></a>**4- Frontend Y Caracteristicas De Firebase**

En este punto voy a explicar un poco en profundidad el *Frontend* de mi aplicación y como he implementado Firebase en ella con más detalle. 

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.024.jpeg) ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.025.jpeg) Las dos pantallas superiores son la pantalla principal y la pantalla donde el usuario  gestiona  sus  tareas  respectivamente.  La  pantalla  principal realmente es una especie de portada, las verdaderas funciones están en la pantalla de tareas. En esta pantalla podemos cambiar de fecha pulsando en la fecha actual, crear tareas pulsando en icono de ‘+’, editarlas pulsando en las propias tareas o eliminarlas deslizando las mismas hacia la izquierda (En el video demostrativo se muestran todas estás funciones en acción). Todas estás funciones son posibles gracias a firebase, mostrare un poco de codigo en cuanto al ejemplo de las tareas:

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.026.png)

Como ya he comentado en el punto [**3.4- Diseño – Arquitectura**](#_page16_x56.70_y81.60) me he servido de varios servicios, este es uno de ellos el cual, entre varias cosas, se encargaba de hacer las conexiones y referencias necesarias para trabajar con firebase en cada momento. Una vez hecho esto podría gestionar las tareas o lo que necesitara en firesbase desde mi app.

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.027.png)

Podemos ver que en mi *TareasService* en la funcion de guardar o editar tareas lo primero que hago es referenciar a la base de datos de firebase y, en este caso, apuntando hacía las tareas.

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.028.png)Tras  haber  hecho  la  referencia,  creamos  o  editamos  la  tarea correspondiente y la añadimos a nuestra base de datos con todos sus atributos. Este proceso para conectarme con Firebase es practicamente el 

mismo cada vez que quiero recuperar, subir o editar datos de esta plataforma, hacer referencia a la herramienta necesaria, especificar el lugar de acción y por ultimo especificar y ejecutar la acción en sí.

**SplashActivity**

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.029.png)

Me gustaría destacar esta pequeña  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.030.jpeg)*SplashScreen*  que  se  encarga  de  cargar los datos en segundo plano  mientras el usuario ve esta pantalla  de carga sencilla. Para sincronizar  la barra de progreso con los datos  simplemente hago que avance poco  a poco hasta un punto en concreto  en el que se detiene hasta que los  datos  se  han  cargado  en  mi  aplicación  completamente,  es  en  ese momento que el progreso se  termina de completarse  rápidamente  dejando  entrar  al  usuario a la app en sí. 

<a name="_page27_x56.70_y81.60"></a>**5- Conclusión**

El desarrollo de ZapeTask usando Android Studio y utilizando Firebase como *Backend*, ha sido un proyecto divertido y desafiante. A lo largo del desarrollo e implementación, enfrenté diversos obstáculos, como el que detallo en el punto [***7- Problemas Durante El Desarrollo***](#_page29_x56.70_y81.60), que pusieron a prueba mis habilidades y conocimientos. Estos problemas, a excepción del que muestro en el punto ya mencionado, fueron errores inesperados que surgieron durante el desarrollo y por fortuna pude solucionar.

A pesar de estos desafíos, el desarrollo de la aplicación ha concluido con éxito. Con cada problema resuelto me atrevo a decir que he aprendido y mejorado mis competencias de programación, al menos en el ámbito móvil. Algo que podría destacar es lo útil que resulta tener una arquitectura de proyecto bien estructurada ya que a la hora de trabajar todo se hace más fácil y sencillo.

El resultado final es una aplicación funcional y robusta que cumple con los objetivos propuestos al inicio del proyecto. Estoy orgulloso del trabajo realizado, pero esto no significa que ZapeTask se quede aquí, tengo cambios  y  nuevas  funciones  pensadas  para  seguir  mejorando  esta aplicación.

<a name="_page28_x56.70_y81.60"></a>**6- Futuras Versiones**

Aunque estoy orgulloso del resultado, hay  ![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.031.png)varias cosas que agregaré poco a poco a la  aplicación. Una de ellas es un sistema que  incentive al usuario a seguir utilizando la  aplicación, sistema liderado por ***Zapet***, la  mascota que le da nombre al proyecto y he  diseñado yo mismo. Este sistema te recompensará por seguir y cumplir las tareas que te propongas dándote como recompensa ***ZapetCoins***, monedas que podrás utilizar dentro de la app para comprarle objetos a nuestro robot e incluso personalizarlo a nuestro gusto.

Otra de las funciones que agregaré es un avisador de objetos. Actualmente si pulsas esta imagen te mostrará un pequeño dialogo en el cual podrás añadir “Objetos Clave” los cuales tu mismo puedes definir pero en este momento no sirve para nada. Próximamente la app accederá a la ubicación de tu dispositivo para avisarte al salir de casa si llevas esos objetos encima, por ejemplo, imagina que sueles olvidar las llaves del coche porque eres muy despistado, en ese caso tu añadirás tus llaves a tu lista de objetos y cuando te alejes unos pocos metros de casa la aplicación te mandaría una notificación a modo de recordatorio cuando aún estas a tiempo de volver a casa.

<a name="_page29_x56.70_y81.60"></a>**7- Problemas Durante El Desarrollo**

La verdad que el desarrollo de la aplicación fue una gráfica exponencial en toda regla, una exponencial negativa. La primera función que quería implementar era la pantalla de tareas la cual utiliza un *TabLayout* con un *Fragment* distinto en cada opción del *Tab*. Creí que no me llevaría mucho tiempo implementarlo pero pronto me tope con grandes problemas. La principal dificultad radicaba en que cada fragmento mostraba tareas de distintos tipos y por lo tanto cada fragmento posee su propio *RecyclerView* propio el cual debía declarar cada vez que el usuario cambiara de opción. Pasé semanas “*crasheando”* la aplicación en bucle sin saber que ocurría pero finalmente descubrí el problema. Resulta que al cambiar de tab una primera vez todo se declaraba correctamente ya que dependiendo de la opción que el usuario eligiera buscaba el *RecyclerView* en el fragmento correspondiente, pero si volvía una segunda vez a un fragmento ya inicializado todo petaba. Al parecer cuando volvía al fragmento por segunda vez se creaba otro fragmento nuevo y la aplicación no sabia en que fragmento buscar el *RecyclerView*. Tras pensar mucho en como solucionarlo pensé en utilizar un *ViewModel*, que ya he mencionado anteriormente, el *FragmentViewModel*.

![](Aspose.Words.4a3f7558-bca9-47a1-b0e5-97ecba008f11.032.png)

Usando este *ViewModel,* el cual persistiría a los cambios de fragmentos, seguía estos pasos:

1. Cuando un fragmento hijo se cargaba llamaba a la función de la interfaz *TareasListener*.
1. Esta función, que se ejecutaba en el fragmento padre, guardaba el fragmento con su nombre en *fragmentMap*  y el *RecyclerView* correspondiente en la lista. 
1. El fragmento padre aparte tenia una lista de 3 boleanos los cuales cada uno hacían referencia a cada fragmento hijo, siendo verdaderos una vez que se hayan inicializado.
1. Es en este punto cuando el usuario al abrir un fragmento hijo por segunda vez la aplicación sabía que ese fragmento ya había sido iniciado y en vez de crear uno nuevo recuperaba el fragmento correspondiente del *ViewModel*.

Una vez solucioné este problema que me llevo semanas, todo el desarrollo se hizo mucho más ameno y sencillo.

<a name="_page31_x56.70_y81.60"></a>**8- Bibliografía**

<https://developer.android.com/develop?hl=es-419> – **Android Studio 
<https://firebase.google.com/docs?hl=es>** – **Firebase** 
