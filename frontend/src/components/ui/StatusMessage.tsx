import type { ReactNode } from 'react';

interface StatusMessageProps {
  type: 'error' | 'success' | 'info';
  children: ReactNode;
}

export function StatusMessage({ type, children }: StatusMessageProps) {
  const className = type === 'error' ? 'form-error' : type === 'success' ? 'form-success' : 'form-info';
  return <p className={className}>{children}</p>;
}
