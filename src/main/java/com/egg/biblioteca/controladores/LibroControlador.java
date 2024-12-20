    package com.egg.biblioteca.controladores;


    import com.egg.biblioteca.entidades.Autor;
    import com.egg.biblioteca.entidades.Editorial;
    import com.egg.biblioteca.entidades.Libro;
    import com.egg.biblioteca.excepciones.MiException;
    import com.egg.biblioteca.servicios.AutorServicio;
    import com.egg.biblioteca.servicios.EditorialServicio;
    import com.egg.biblioteca.servicios.LibroServicio;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.ModelMap;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.logging.Level;
    import java.util.logging.Logger;

    @Controller
    @RequestMapping("/libro")
    public class LibroControlador {

        @Autowired
        private LibroServicio libroServicio;
        @Autowired
        private AutorServicio autorServicio;
        @Autowired
        private EditorialServicio editorialServicio;

        @GetMapping("/registrar") // localhost:8080/libro/registrar
        public String registrar(ModelMap model) {
            List<Autor> autores = autorServicio.listarAutores();
            List<Editorial> editoriales = editorialServicio.listarEditoriales();
            model.addAttribute("autores", autores);
            model.addAttribute("editoriales", editoriales);
            return "libro_form.html";
        }

        @PostMapping("/registro")
        public String registro(@RequestParam Long isbn ,@RequestParam String titulo,
                               @RequestParam(required = false) Integer ejemplares, @RequestParam String idAutor,
                               @RequestParam String idEditorial, ModelMap modelo){

            try{
                libroServicio.crearLibro(isbn, titulo, ejemplares, idAutor, idEditorial);

                modelo.put("exito","EL libro fue cargado correctamente!" );
            } catch (MiException ex){
                modelo.put("error", ex.getMessage());
                Logger.getLogger(LibroControlador.class.getName()).log(Level.SEVERE, null, ex);
                return "libro_form.html";
            }
            return "index.html";
        }

        @GetMapping("/lista")
        public String listar(ModelMap model){
            List<Libro> libros = libroServicio.listarLibros();
            model.addAttribute("libros", libros);
            return "libro_list.html";
        }


        @GetMapping("/modificar/{isbn}")
        public String modificar(@PathVariable Long isbn, ModelMap modelo) {
            modelo.put("libro", libroServicio.getOne(isbn));

            List<Autor> autores = autorServicio.listarAutores();
            List<Editorial> editoriales = editorialServicio.listarEditoriales();

            modelo.addAttribute("autores", autores);
            modelo.addAttribute("editoriales", editoriales);
            return "libro_modificar.html";
        }



        @PostMapping("/modificar/{isbn}")

        public String modificar(@PathVariable Long isbn, @RequestParam String titulo,
                                @RequestParam Integer ejemplares, @RequestParam String idAutor,
                                @RequestParam String idEditorial, ModelMap modelo){
            try {
                List<Autor> autores = autorServicio.listarAutores();
                List<Editorial> editoriales = editorialServicio.listarEditoriales();

                modelo.addAttribute("autores", autores);
                modelo.addAttribute("editoriales", editoriales);

                libroServicio.modificarLibro(isbn, titulo, ejemplares, idAutor, idEditorial);
                modelo.addAttribute("exito", "Libro modificado exitosamente");
                return "redirect:../lista";
            } catch (MiException e) {
                List<Autor> autores = autorServicio.listarAutores();
                List<Editorial> editoriales = editorialServicio.listarEditoriales();

                modelo.put("error", e.getMessage());

                modelo.addAttribute("autores", autores);
                modelo.addAttribute("editoriales", editoriales);
                return "libro_modificar.html";
            }
        }


    }