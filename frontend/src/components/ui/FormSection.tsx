import type { ReactNode } from 'react';

interface FormSectionProps {
  step?: string;
  title: string;
  description?: string;
  children: ReactNode;
}

export function FormSection({ step, title, description, children }: FormSectionProps) {
  return (
    <fieldset className="form-section">
      <legend>
        {step && <span>{step}</span>}
        {title}
      </legend>
      {description && <p>{description}</p>}
      <div className="form-grid">{children}</div>
    </fieldset>
  );
}
