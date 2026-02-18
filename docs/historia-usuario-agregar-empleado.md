# Historia de Usuario: Agregar empleado con credenciales de acceso

**Como** administrador de RRHH  
**Quiero** poder registrar nuevos empleados con sus datos personales y credenciales de acceso  
**Para** que puedan ingresar al sistema y gestionar su información.

---

## Criterios de aceptación

- **Dado** que estoy logueado como administrador  
- **Cuando** navego al módulo PIM > Add Employee  
- **Entonces** debo ver el formulario de registro con los campos: First Name, Middle Name, Last Name, Employee Id, Create Login Details (switch), Username, Password, Confirm Password y Status.

- **Dado** que estoy en el formulario Add Employee  
- **Cuando** diligencio First Name, Middle Name, Last Name, Employee Id y activo Create Login Details con Username, Password y Status = Enabled  
- **Entonces** al guardar debo ver un mensaje de éxito y el empleado debe aparecer en la lista de empleados.

- **Dado** que un empleado fue registrado correctamente  
- **Cuando** busco en Employee List por nombre (middle name) e Employee Id  
- **Entonces** debo ver el empleado en la tabla de resultados con su Id, nombre y apellido.

---

## Definición de terminado

- [ ] El empleado se guarda correctamente en la base de datos
- [ ] Las credenciales permiten el login del nuevo empleado
- [ ] El empleado aparece en la búsqueda de la lista de empleados
- [ ] Los tests automatizados pasan

---

## Notas técnicas

- **Prioridad:** Alta
- **Estimación:** 5 puntos
- **Feature:** `agregar_empleado.feature`
