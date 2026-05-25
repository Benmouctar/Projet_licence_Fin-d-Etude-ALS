import React from 'react';
import {
  CheckCircle,
  Lock,
  AlertTriangle,
  BookOpen,
  FileText,
  HelpCircle,
  Video,
  Loader2,
} from 'lucide-react';
import { useModuleAccess } from '../../hooks/useModuleAccess';
import type { Module, Enrollment } from '../../types';

/**
 * Reusable React presentation component that renders the Module Status Button user interface element.
 */
interface ModuleStatusButtonProps {
  module: Module;
  enrollment: Enrollment | undefined;
  isCurrent: boolean;
  isCompleted: boolean;
  onClick: () => void;
  
  index: number;
  
  hasQuizModules: boolean;
}


const TypeIcon: React.FC<{ type: Module['type']; className?: string }> = ({ type, className }) => {
  switch (type) {
    case 'quiz':  return <HelpCircle className={className} />;
    case 'pdf':   return <FileText   className={className} />;
    case 'video': return <Video      className={className} />;
    default:      return <BookOpen   className={className} />;
  }
};










export const ModuleStatusButton: React.FC<ModuleStatusButtonProps> = ({
  module,
  enrollment,
  isCurrent,
  isCompleted,
  onClick,
  index,
  hasQuizModules,
}) => {
  const { data: access, isLoading: accessLoading } = useModuleAccess(
    enrollment?.id ?? '',
    module.id
  );

  
  const alwaysAccessible = index === 0 || !hasQuizModules;

  
  const isLocked    = !alwaysAccessible && !isCompleted && access !== undefined && !access.canAccess;
  const needsReview = !isLocked && !alwaysAccessible && access?.reason?.toLowerCase().includes('tutor');
  const lockReason  = access?.reason ?? '';

  
  
  if (accessLoading && !isCompleted && !isCurrent && !alwaysAccessible) {
    return (
      <div className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-slate-400 dark:text-slate-600">
        <Loader2 className="w-4 h-4 shrink-0 animate-spin" />
        <span className="truncate text-sm font-medium opacity-60">{module.title}</span>
      </div>
    );
  }

  
  if (isCompleted) {
    return (
      <button
        type="button"
        title={module.title}
        onClick={onClick}
        className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left text-sm font-medium transition-all ${
          isCurrent
            ? 'bg-emerald-50 dark:bg-emerald-500/10 text-emerald-700 dark:text-emerald-400'
            : 'text-emerald-600 dark:text-emerald-400 hover:bg-emerald-50/60 dark:hover:bg-emerald-500/5'
        }`}
      >
        <CheckCircle className="w-4 h-4 shrink-0 text-emerald-500" />
        <span className="truncate">{module.title}</span>
      </button>
    );
  }

  
  if (isLocked) {
    return (
      <button
        type="button"
        title={lockReason || 'This module is locked'}
        disabled
        aria-disabled="true"
        className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left text-sm font-medium
          opacity-50 cursor-not-allowed select-none
          text-slate-500 dark:text-slate-500
          bg-slate-50/60 dark:bg-white/[0.03]"
      >
        <Lock className="w-4 h-4 shrink-0 text-slate-400 dark:text-slate-600" />
        <span className="truncate flex-1">{module.title}</span>
        <span className="shrink-0 text-[10px] font-bold uppercase tracking-wide
          bg-slate-200 dark:bg-white/10 text-slate-500 dark:text-slate-400
          px-1.5 py-0.5 rounded-md">
          Locked
        </span>
      </button>
    );
  }

  
  if (needsReview) {
    return (
      <button
        type="button"
        title={lockReason}
        onClick={onClick}
        className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left text-sm font-medium transition-all ${
          isCurrent
            ? 'bg-amber-50 dark:bg-amber-500/10 text-amber-700 dark:text-amber-400'
            : 'text-amber-600 dark:text-amber-400 hover:bg-amber-50/60 dark:hover:bg-amber-500/5'
        }`}
      >
        <AlertTriangle className="w-4 h-4 shrink-0" />
        <span className="truncate">{module.title}</span>
      </button>
    );
  }

  
  return (
    <button
      type="button"
      title={module.title}
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-left text-sm font-medium transition-all ${
        isCurrent
          ? 'bg-emerald-50 dark:bg-emerald-500/10 text-emerald-700 dark:text-emerald-400'
          : 'text-slate-600 dark:text-slate-400 hover:bg-slate-50 dark:hover:bg-white/5'
      }`}
    >
      <TypeIcon type={module.type} className="w-4 h-4 shrink-0" />
      <span className="truncate">{module.title}</span>
    </button>
  );
};
