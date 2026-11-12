import { useState } from 'react';
import Campo, { claseInput, AccionesFormulario } from '../ui/Campo';

// Formulario que se arma a partir de una lista de campos. Ejemplo:
// campos = [
//   { nombre: 'idCarrera', etiqueta: 'Código', requerido: true, max: 20, soloAlCrear: true },
//   { nombre: 'idNivel', etiqueta: 'Nivel', tipo: 'select', opciones: [{ valor: 'NIV001', texto: 'Primer semestre' }] },
// ]
// Al guardar, los campos opcionales vacíos se envían como null y los numéricos como número,
// tal como los espera el backend.
export default function FormularioEntidad({ campos, inicial, editando, onGuardar, onCancelar, guardando }) {
  const [form, setForm] = useState(() => {
    const valores = {};
    campos.forEach((c) => {
      const valor = inicial?.[c.nombre];
      if (c.tipo === 'checkbox') valores[c.nombre] = valor ?? c.porDefecto ?? true;
      else valores[c.nombre] = valor ?? c.porDefecto ?? (c.tipo === 'select' && c.requerido ? c.opciones[0]?.valor ?? '' : '');
    });
    return valores;
  });

  const cambiar = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({ ...prev, [name]: type === 'checkbox' ? checked : value }));
  };

  const enviar = (e) => {
    e.preventDefault();
    const datos = {};
    campos.forEach((c) => {
      const valor = form[c.nombre];
      if (c.tipo === 'checkbox') datos[c.nombre] = valor;
      else if (valor === '' || valor === null || valor === undefined) datos[c.nombre] = null;
      else if (c.tipo === 'number') datos[c.nombre] = Number(valor);
      else datos[c.nombre] = typeof valor === 'string' ? valor.trim() : valor;
    });
    onGuardar(datos);
  };

  return (
    <form onSubmit={enviar} className="space-y-4">
      <div className="grid gap-4 sm:grid-cols-2">
        {campos.map((c) => {
          const id = `campo-${c.nombre}`;
          const deshabilitado = editando && c.soloAlCrear;
          const ancho = c.anchoCompleto ? 'sm:col-span-2' : '';

          if (c.tipo === 'checkbox') {
            return (
              <label key={c.nombre} htmlFor={id} className={`flex items-center gap-2 text-sm font-medium ${ancho}`}>
                <input id={id} type="checkbox" name={c.nombre} checked={form[c.nombre]} onChange={cambiar} className="h-4 w-4 accent-andino" />
                {c.etiqueta}
              </label>
            );
          }

          return (
            <div key={c.nombre} className={ancho}>
              <Campo etiqueta={c.requerido ? c.etiqueta : `${c.etiqueta} (opcional)`} id={id} ayuda={deshabilitado ? 'El código no se puede cambiar.' : c.ayuda}>
                {c.tipo === 'select' ? (
                  <select id={id} name={c.nombre} value={form[c.nombre]} onChange={cambiar} required={c.requerido} disabled={deshabilitado} className={claseInput}>
                    {!c.requerido && <option value="">Sin especificar</option>}
                    {c.opciones.map((o) => (
                      <option key={o.valor} value={o.valor}>{o.texto}</option>
                    ))}
                  </select>
                ) : (
                  <input
                    id={id}
                    name={c.nombre}
                    type={c.tipo ?? 'text'}
                    value={form[c.nombre]}
                    onChange={cambiar}
                    required={c.requerido}
                    disabled={deshabilitado}
                    maxLength={c.max}
                    min={c.min}
                    step={c.step}
                    placeholder={c.placeholder}
                    className={claseInput}
                  />
                )}
              </Campo>
            </div>
          );
        })}
      </div>
      <AccionesFormulario onCancelar={onCancelar} guardando={guardando} />
    </form>
  );
}
