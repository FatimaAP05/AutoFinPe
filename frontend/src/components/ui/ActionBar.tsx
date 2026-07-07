import type { ReactNode } from 'react';

interface ActionBarProps {
  children: ReactNode;
  align?: 'start' | 'end' | 'between';
}

export function ActionBar({ children, align = 'start' }: ActionBarProps) {
  return <div className={`action-bar action-bar-${align}`}>{children}</div>;
}
