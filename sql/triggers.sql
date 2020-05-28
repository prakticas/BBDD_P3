/**Actualizar desvios y retrasos al poner Incidencias**/
CREATE or REPLACE TRIGGER NEWRETRASO
BEFORE INSERT ON INCIDENCIA
FOR EACH ROW
WHEN (new.tipo = 'retrasado')
  BEGIN
  INSERT INTO RETRASO(id) VALUES (:new.id);
  raise_application_error(-20001,'Ahora se debería especificar el tiempo de retraso');
END NEWRETRASO;
/

CREATE or REPLACE TRIGGER NEWDESVIO
BEFORE INSERT ON INCIDENCIA
FOR EACH ROW
WHEN ((new.tipo = 'desviado1') OR (new.tipo = 'desviado2'))
  BEGIN
  INSERT INTO DESVIO(id) VALUES (:new.id);
  raise_application_error(-20001,'Ahora se debería especificar el nuevo avion y nuevo aeropuerto');
END NEWDESVIO;
/
