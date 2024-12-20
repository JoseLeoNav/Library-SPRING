package com.egg.biblioteca.servicios;

import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.entidades.Libro;
import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.enumeraciones.Rol;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional
    public void registrar(String nombre, String email, String password, String password2, MultipartFile archivo) throws MiException {

        validar(nombre, email, password, password2);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);
        Imagen imagen =imagenServicio.guardar(archivo);
        usuario.setImagen(imagen);
        usuarioRepositorio.save(usuario);

    }


    private void validar(String nombre, String email, String password, String password2) throws MiException {


        if (nombre.isEmpty() || nombre == null) {
            throw new MiException("el nombre no puede ser nulo o estar vacío");
        }
        if (email.isEmpty() || email == null) {
            throw new MiException("el email no puede ser nulo o estar vacío");
        }
        if (password.isEmpty() || password == null || password.length() <= 5) {
            throw new MiException("La contraseña no puede estar vacía, y debe tener más de 5 dígitos");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if(usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" +usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession sesion = attr.getRequest().getSession(true);
            sesion.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getEmail(), usuario.getPassword(), permisos );
            return user;
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Usuario getOne( String id){
        return usuarioRepositorio.getReferenceById(id);
    }


    @Transactional
    public void actualizarUsuario(String id, String nombre, String email, String nuevaPassword, String nuevaPassword2, MultipartFile archivo) throws MiException {

        // Validar los datos proporcionados (nombre, email y contraseñas)
        validar(nombre, email, nuevaPassword, nuevaPassword2);

        // Obtener el usuario desde la base de datos
        Usuario usuario = usuarioRepositorio.findById(id).orElseThrow(() -> new MiException("No se encontró el usuario con el ID proporcionado"));

        // Actualizar nombre y email
        usuario.setNombre(nombre);
        usuario.setEmail(email);

        // Verificar si se desea actualizar la contraseña
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


            // Verificar que la nueva contraseña coincida con su confirmación
            if (!nuevaPassword.equals(nuevaPassword2)) {
                throw new MiException("Las nuevas contraseñas no coinciden");
            }

            // Actualizar la contraseña
            usuario.setPassword(encoder.encode(nuevaPassword));
        }

        Imagen imagen =imagenServicio.guardar(archivo);
        usuario.setImagen(imagen);
        // Guardar los cambios en la base de datos
        usuarioRepositorio.save(usuario);
    }



}
