# Descripción 
Aplicación para Android que permite manipular imagenes, entre lo que se puede hacer está:
- Tomar imagenes con la camara
- Cudricular la imagen
- Rotar imagen
- Comprimir imagen para reducir el peso en KB
- Enviar imagen a un servidor

# Capturas de pantalla

- Para tomar la imagen se utiliza la camara, se usa la libreria 
MagicalCamera, un Layout con un ProgressBar y un texto aparecen mientras la imagen es cargada.

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/cargando_img.jpeg"  width="300px" >

- Al cuadricular la imagen esta se deforma para tomar convertirce en un cuadrado.

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/tomar_img.jpeg"  width="300px" > 
<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/cuadricular_img.jpeg"  width="300px" >

- Girar la imagen 90°

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/rotal_img.jpeg"  width="300px" >

- Comprimir imagen para bajar el peso, en este caso pasó de 1954 KB a 48 KB, se utiliza la libreria Compressor

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/bajar_peso_img.jpeg"  width="300px" >

- Al guardar la imagen se crea (en caso que no lo esté) una carpeta llamada "Imagenes" y ahí dentro estará

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/carpeta_img_guardada.jpeg"  width="300px" >

- Al enviar imagenes se lanza un IntentService encargado de este proceso, se crea una notificación y se actualiza a medida
que se envian las imagenes

<img src="https://crissalvarezh.github.io/ImagenesRepos/imgs/manipulador_imgs/notiticacion_envio_img.jpeg"  width="300px" >

# Proximas mejoras

- Dejar de usar libreria MagicalCamera para agilizar el guardado y mostrado de las imagenes
- Agregar soporte para Android N y las restricción en el URI al momento de tomar imagenes con la camara
- Agregar opción de seleccionar imagen de galeria
- Dejar de guardar en un SharedPreferences los estados del servicio de enviar imagenes, debido a que el onDestroy no siempre se llama

