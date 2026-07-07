import { helpTexts, type HelpTextKey } from '../../constants/helpTexts';
import { Tooltip } from './Tooltip';

interface HelpLabelProps {
  children: string;
  helpKey: HelpTextKey;
}

export function HelpLabel({ children, helpKey }: HelpLabelProps) {
  return (
    <span className="help-label">
      <span>{children}</span>
      <Tooltip>{helpTexts[helpKey]}</Tooltip>
    </span>
  );
}
