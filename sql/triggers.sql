/**Actualizar desvios y retrasos al poner Incidencias**/
SET SERVEROUTPUT ON

CREATE or REPLACE TRIGGER NEWRETRASO
AFTER INSERT ON INCIDENCIA
FOR EACH ROW
WHEN (new.tipo = 'retrasado')
  BEGIN
  INSERT INTO RETRASO(id) VALUES (:new.id);
  DBMS_OUTPUT.put_line ('Ahora se debería especificar el tiempo de retraso');
END NEWRETRASO;
/

INSERT INTO INCIDENCIA (id,tipo,vuelo) values (123458,'desviado2',31398)

CREATE or REPLACE TRIGGER NEWDESVIO
AFTER INSERT ON INCIDENCIA
FOR EACH ROW
WHEN ((new.tipo = 'desviado1') OR (new.tipo = 'desviado2'))
  BEGIN
  INSERT INTO DESVIO(id) VALUES (:new.id);
  DBMS_OUTPUT.put_line ('Ahora se debería especificar el nuevo avion y nuevo aeropuerto');
END NEWDESVIO;
/
