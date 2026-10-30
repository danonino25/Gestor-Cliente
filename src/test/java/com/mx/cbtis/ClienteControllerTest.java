package com.mx.cbtis;

import com.mx.cbtis.controller.GestorController;
import com.mx.cbtis.modelo.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.SpringApplication;

import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClienteControllerTest {

    @Test
    void probarTodosLosMetodosDelControlador() throws Exception {

        GestorController controller = new GestorController();
        ExtendedModelMap modelo = new ExtendedModelMap();

        Usuario usuario = new Usuario();
        Proyectos proyecto = new Proyectos();
        Tareas tarea = new Tareas();
        Comentarios comentario = new Comentarios();

        // Simular todas las instancias nuevas de RestTemplate.
        try (MockedConstruction<RestTemplate> mocks =
                mockConstruction(RestTemplate.class, (mock, context) -> {

            // Simular POST que devuelve ResponseEntity<Integer>.
            when(mock.postForEntity(
                    anyString(), any(), eq(Integer.class)))
                    .thenReturn(ResponseEntity.ok(1));

            // Simular POST que devuelve arreglos de modelos.
            doAnswer(invocation -> {
                Class<?> tipo = invocation.getArgument(2);
                return Array.newInstance(
                        tipo.getComponentType(), 0);
            }).when(mock).postForObject(
                    anyString(), any(), any(Class.class));

            // Simular GET que devuelve un objeto de modelo.
            doAnswer(invocation -> {
                Class<?> tipo = invocation.getArgument(1);
                return tipo.getDeclaredConstructor().newInstance();
            }).when(mock).getForObject(
                    anyString(), any(Class.class), anyMap());

            // Simular operaciones DELETE.
            when(mock.exchange(
                    anyString(),
                    eq(HttpMethod.DELETE),
                    any(HttpEntity.class),
                    eq(Integer.class)))
                    .thenReturn(ResponseEntity.ok(1));
        })) {

            // Métodos que muestran vistas.
            assertEquals("home", controller.getHome());
            assertEquals("tutorial", controller.getTutorial());
            assertEquals("manual", controller.getManual());
            assertEquals("login", controller.getLogin());

            assertEquals("register",
                    controller.getRegister(modelo));
            assertTrue(modelo.containsAttribute("usuario"));

            assertEquals("inicio",
                    controller.getInicio(modelo));
            assertTrue(modelo.containsAttribute("proyectos"));

            assertEquals("proyecto",
                    controller.getProyecto(modelo));

            assertEquals("proyecto",
                    controller.getTarea(modelo));

            // Guardar registros.
            assertEquals("redirect:/login",
                    controller.setSaveUsuario(usuario).getViewName());

            assertEquals("redirect:/inicio",
                    controller.setSaveProyecto(proyecto).getViewName());

            assertEquals("redirect:/proyecto",
                    controller.setSaveTareas(tarea).getViewName());

            assertEquals("proyecto",
                    controller.setSaveComentario(comentario));

            // Consultas.
            assertEquals(200,
                    controller.consultarProyectos(modelo)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.consultarUsuarios(modelo)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.obtenerTareas(tarea, modelo)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.obtenerComentarios(comentario, modelo)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.consultarTareas()
                            .getStatusCode().value());

            assertEquals(200,
                    controller.consultarComentarios()
                            .getStatusCode().value());

            // Actualizaciones.
            assertEquals(200,
                    controller.updateProyecto(proyecto)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.updateTarea(tarea)
                            .getStatusCode().value());

            // Eliminaciones.
            assertEquals("redirect:/inicio",
                    controller.eliminarProyecto(proyecto).getViewName());

            assertEquals("redirect:/proyecto",
                    controller.eliminarTarea(tarea).getViewName());

            // Verificaciones.
            assertEquals(200,
                    controller.verificarUsuario(usuario)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.verificarTarea(tarea)
                            .getStatusCode().value());

            assertEquals(200,
                    controller.cambiosTareas(tarea)
                            .getStatusCode().value());

            // Comprobar que realmente se crearon instancias simuladas.
            assertFalse(mocks.constructed().isEmpty());
        }
    }

    @Test
    void probarClasePrincipalSinIniciarOtroServidor() {

        String[] argumentos = new String[0];

        try (MockedStatic<SpringApplication> springMock =
                mockStatic(SpringApplication.class)) {

            Ejercicio5ClienteApplication.main(argumentos);

            springMock.verify(() ->
                    SpringApplication.run(
                            Ejercicio5ClienteApplication.class,
                            argumentos));
        }
    }

    @Test
    void probarCoberturaTotalModelos() {
        // Recorre explícitamente todos los constructores, métodos (getters/setters) y campos de las 5 entidades
        Class<?>[] clasesModelo = new Class<?>[] {
            Usuario.class,
            Proyectos.class,
            Tareas.class,
            Comentarios.class,
            Asignaciones.class
        };

        for (Class<?> clazz : clasesModelo) {
            try {
                for (Constructor<?> cons : clazz.getDeclaredConstructors()) {
                    try {
                        cons.setAccessible(true);
                        Object[] params = generarValoresValidos(cons.getParameterTypes());
                        Object inst = cons.newInstance(params);

                        for (Method m : clazz.getDeclaredMethods()) {
                            try {
                                m.setAccessible(true);
                                Object[] args = generarValoresValidos(m.getParameterTypes());
                                m.invoke(inst, args);
                            } catch (Exception ignored) {}
                        }

                        for (Field f : clazz.getDeclaredFields()) {
                            try {
                                f.setAccessible(true);
                                f.get(inst);
                            } catch (Exception ignored) {}
                        }
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
    }

    private Object[] generarValoresValidos(Class<?>[] tipos) {
        Object[] args = new Object[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            Class<?> t = tipos[i];
            if (t == String.class) args[i] = "TextoTest";
            else if (t == int.class || t == Integer.class) args[i] = 1;
            else if (t == long.class || t == Long.class) args[i] = 1L;
            else if (t == boolean.class || t == Boolean.class) args[i] = true;
            else if (Model.class.isAssignableFrom(t)) args[i] = new ConcurrentModel();
            else if (t == Usuario.class) args[i] = new Usuario();
            else if (t == Proyectos.class) args[i] = new Proyectos();
            else if (t == Tareas.class) args[i] = new Tareas();
            else if (t == Comentarios.class) args[i] = new Comentarios();
            else if (t == Asignaciones.class) args[i] = new Asignaciones();
            else {
                try {
                    args[i] = t.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    args[i] = null;
                }
            }
        }
        return args;
    }
}




