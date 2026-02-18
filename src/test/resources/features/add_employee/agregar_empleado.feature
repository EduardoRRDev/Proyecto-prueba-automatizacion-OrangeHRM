# language: es

Característica: Agregar empleado
  Yo como administrador de RRHH necesito agregar nuevos empleados
  en el sistema a través del módulo PIM.

  # Los datos del empleado se cargan desde src/test/resources/data/employees.properties
  @AgregarEmpleado
  Escenario: Agregar empleado con datos básicos
    Dado que el usuario navega a la página de inicio de sesión
    Y ingresa las credenciales de acceso correctas
    Cuando navega a la página Add Employee
    Y diligencia los datos del formulario
    Y guarda el empleado
    Entonces verifica que el empleado se guardó correctamente
    Y navega a la lista de empleados
    Y busca el empleado registrado
    Entonces verifica que el empleado aparece en la tabla
    Y elimina el empleado de la lista

