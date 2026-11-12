import { useState } from 'react';
import Encabezado from './ui/Encabezado';
import Alerta from './ui/Alerta';
import Boton from './ui/Boton';
import Buscador from './ui/Buscador';
import Tabla from './ui/Tabla';
import Modal from './ui/Modal';
import Confirmar from './ui/Confirmar';
import FormularioEntidad from './forms/FormularioEntidad';
import useCarga from '../hooks/useCarga';
import { mensajeError } from '../utils/errores';

// Pantalla completa de listar, buscar, crear, editar y eliminar.
// La usan Carreras, Materias, Niveles, Profesores, Estudiantes, Grupos, Inscripciones y más.
//
// Props principales:
//  - cargar: función que trae la lista del backend
//  - crear / actualizar / eliminar: funciones del servicio (si falta una, no se muestra ese botón)
//  - idDe(fila): identificador del registro;  nombreDe(fila): texto para el mensaje de confirmación
//  - columnas: columnas de la tabla;  campos: campos del formulario (lista o función de la fila editada)
//  - textoBusqueda(fila): texto en el que busca el buscador
//  - Formulario: formulario propio, si los campos genéricos no alcanzan
export default function PaginaCrud({
  titulo,
  descripcion,
  entidad,
  femenino = false,
  cargar,
  dependencias = [],
  crear,
  actualizar,
  eliminar,
  idDe,
  nombreDe,
  columnas,
  campos,
  textoBusqueda,
  Formulario,
  accionesExtra,
  aviso,
  children,
}) {
  const { datos, cargando, error, setError, recargar } = useCarga(cargar, dependencias);
  const [busqueda, setBusqueda] = useState('');
  const [formAbierto, setFormAbierto] = useState(false);
  const [editando, setEditando] = useState(null);
  const [porEliminar, setPorEliminar] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [exito, setExito] = useState(null);

  const filas = textoBusqueda && busqueda
    ? datos.filter((f) => textoBusqueda(f).toLowerCase().includes(busqueda.toLowerCase()))
    : datos;

  const abrirNuevo = () => {
    setEditando(null);
    setFormAbierto(true);
  };

  const abrirEdicion = (fila) => {
    setEditando(fila);
    setFormAbierto(true);
  };

  const guardar = async (valores) => {
    setGuardando(true);
    try {
      if (editando) {
        await actualizar(idDe(editando), valores);
        setExito(`${entidad} actualizad${fin} correctamente.`);
      } else {
        await crear(valores);
        setExito(`${entidad} cread${femenino ? 'a' : 'o'} correctamente.`);
      }
      setFormAbierto(false);
      setError(null);
      await recargar();
    } catch (err) {
      setError(mensajeError(err));
      setFormAbierto(false);
    } finally {
      setGuardando(false);
    }
  };

  const confirmarEliminar = async () => {
    const fila = porEliminar;
    setPorEliminar(null);
    try {
      await eliminar(idDe(fila));
      setExito(`${entidad} eliminad${femenino ? 'a' : 'o'}.`);
      setError(null);
      await recargar();
    } catch (err) {
      setError(mensajeError(err));
    }
  };

  const fin = femenino ? 'a' : 'o';

  const camposFormulario = typeof campos === 'function' ? campos(editando) : campos;

  const acciones = actualizar || eliminar || accionesExtra
    ? (fila) => (
        <div className="flex justify-end gap-1">
          {accionesExtra?.(fila, recargar)}
          {actualizar && <Boton variante="texto" tamano="pequeno" onClick={() => abrirEdicion(fila)}>Editar</Boton>}
          {eliminar && <Boton variante="textoPeligro" tamano="pequeno" onClick={() => setPorEliminar(fila)}>Eliminar</Boton>}
        </div>
      )
    : undefined;

  return (
    <>
      <Encabezado titulo={titulo} descripcion={descripcion}>
        {crear && <Boton onClick={abrirNuevo}>Agregar {entidad.toLowerCase()}</Boton>}
      </Encabezado>

      <Alerta tipo="error" onCerrar={() => setError(null)}>{error}</Alerta>
      <Alerta tipo="exito" onCerrar={() => setExito(null)}>{exito}</Alerta>
      {aviso && <Alerta tipo="aviso">{aviso}</Alerta>}

      {textoBusqueda && <Buscador valor={busqueda} onCambiar={setBusqueda} placeholder={`Buscar en ${titulo.toLowerCase()}…`} />}

      <Tabla
        columnas={columnas}
        filas={filas}
        claveFila={idDe}
        cargando={cargando}
        acciones={acciones}
        vacio={busqueda ? 'Ningún registro coincide con la búsqueda.' : `Todavía no hay ${titulo.toLowerCase()} registrad${fin}s.`}
      />

      {children}

      <Modal abierto={formAbierto} titulo={editando ? `Editar ${entidad.toLowerCase()}` : `Agregar ${entidad.toLowerCase()}`} onCerrar={() => setFormAbierto(false)}>
        {formAbierto && (Formulario ? (
          <Formulario inicial={editando} editando={Boolean(editando)} onGuardar={guardar} onCancelar={() => setFormAbierto(false)} guardando={guardando} />
        ) : (
          <FormularioEntidad
            key={editando ? idDe(editando) : 'nuevo'}
            campos={camposFormulario}
            inicial={editando}
            editando={Boolean(editando)}
            onGuardar={guardar}
            onCancelar={() => setFormAbierto(false)}
            guardando={guardando}
          />
        ))}
      </Modal>

      <Confirmar
        abierto={Boolean(porEliminar)}
        titulo={`Eliminar ${entidad.toLowerCase()}`}
        mensaje={porEliminar ? `¿Seguro que quieres eliminar "${nombreDe(porEliminar)}"? Esta acción no se puede deshacer.` : ''}
        onConfirmar={confirmarEliminar}
        onCancelar={() => setPorEliminar(null)}
      />
    </>
  );
}
