import Encabezado from '../../components/ui/Encabezado';
import Alerta from '../../components/ui/Alerta';
import Boton from '../../components/ui/Boton';
import BoletinVista from '../../components/BoletinVista';
import useCarga from '../../hooks/useCarga';
import { useAuth } from '../../context/AuthContext';
import { getBoletinEstudiante } from '../../services/reportemysqlServices';

export default function MiBoletin() {
  const { usuario } = useAuth();
  const { datos: boletin, cargando, error } = useCarga(() => getBoletinEstudiante(usuario.idEstudiante), [usuario.idEstudiante], null);

  return (
    <>
      <div className="no-imprimir">
        <Encabezado titulo="Mi boletín" descripcion="Resumen de tu semestre. Puedes imprimirlo o guardarlo como PDF.">
          <Boton onClick={() => window.print()} disabled={!boletin}>Imprimir o guardar PDF</Boton>
        </Encabezado>
      </div>
      <Alerta tipo="error">{error}</Alerta>
      {cargando && <p className="text-gris">Cargando tu boletín…</p>}
      {boletin && <BoletinVista boletin={boletin} />}
    </>
  );
}
