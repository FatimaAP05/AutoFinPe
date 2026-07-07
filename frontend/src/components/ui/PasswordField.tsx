import { useState } from 'react';

interface PasswordFieldProps {
  id: string;
  name: string;
  value: string;
  label?: string;
  autoComplete?: string;
  required?: boolean;
  onChange: (value: string) => void;
}

export function PasswordField({
  id,
  name,
  value,
  label = 'Clave',
  autoComplete = 'current-password',
  required = false,
  onChange,
}: PasswordFieldProps) {
  const [isVisible, setIsVisible] = useState(false);

  return (
    <label className="password-field" htmlFor={id}>
      <span>{label}</span>
      <span className="password-input-wrap">
        <input
          id={id}
          name={name}
          type={isVisible ? 'text' : 'password'}
          autoComplete={autoComplete}
          value={value}
          onChange={(event) => onChange(event.target.value)}
          required={required}
        />
        <button
          className="password-toggle"
          type="button"
          onClick={() => setIsVisible((current) => !current)}
          aria-label={isVisible ? 'Ocultar clave' : 'Mostrar clave'}
        >
          {isVisible ? 'Ocultar' : 'Mostrar'}
        </button>
      </span>
    </label>
  );
}
