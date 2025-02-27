package com.egg.biblioteca.controladores;


import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.servicios.UsuarioServicio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @GetMapping("/registrar")
    public String registrar(){
        return "registro.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam String email, @RequestParam String password, String password2, ModelMap modelo,
                           MultipartFile archivo){

        try {
            usuarioServicio.registrar(nombre, email, password, password2, archivo);
            modelo.put("exito", "Usuario registrado correctamente");
            return "index.html";
        } catch (MiException ex){
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("email", email);
            return "registro.html";
        }
    }


    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo ) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña inválidos!");        }
        return "login.html";
    }



    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/inicio")
    public String inicio(HttpSession sesion) {
       Usuario logueado = (Usuario) sesion.getAttribute("usuariosession");
       if(logueado.getRol().toString().equals("ADMIN")){
           return "redirect:/admin/dashboard";
       }
        return "inicio.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/perfil")
    public String perfil(ModelMap model, HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        model.put("usuario", usuario);
        return "usuario_modificar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/perfil/{id}")
    public String actualizar(@PathVariable String id,@RequestParam String nombre, @RequestParam String email,
                             @RequestParam String password, @RequestParam String password2, MultipartFile archivo, ModelMap model) throws MiException {

        try {
            usuarioServicio.actualizarUsuario(id, nombre, email, password, password2, archivo);
            model.put("exito", "Usuario actualizado correctamente!");
            return "inicio.html";
        } catch (MiException ex){
            model.put("error", ex.getMessage());
            model.put("nombre", nombre);
            model.put("email", email);
        }
        return "usuario_modificar.html";
    }



}
