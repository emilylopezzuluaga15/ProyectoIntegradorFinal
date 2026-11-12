import Modal from './Modal';
import Boton from './Boton';

// Pide confirmación antes de una acción que no se puede deshacer, como eliminar.
export default function Confirmar({ abierto, titulo = 'Confirmar', mensaje, textoAccion = 'Eliminar', onConfirmar, onCancelar }) {
  return (
    <Modal abierto={abierto} titulo={titulo} onCerrar={onCancelar} ancho="max-w-md">
      <p className="text-sm text-gris">{mensaje}</p>
      <div className="mt-6 flex justify-end gap-2">
        <Boton variante="secundario" onClick={onCancelar}>Cancelar</Boton>
        <Boton variante="peligro" onClick={onConfirmar}>{textoAccion}</Boton>
      </div>
    </Modal>
  );
}
