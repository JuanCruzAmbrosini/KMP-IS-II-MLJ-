import React from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { CheckCircle2, AlertTriangle, AlertOctagon, Info, ShieldAlert } from 'lucide-react';

export const ToastPortal = () => {
  const { toasts } = useRestaurant();

  if (!toasts || toasts.length === 0) return null;

  const getIcon = (type) => {
    switch (type) {
      case 'success':
        return <CheckCircle2 size={20} color="#10b981" />;
      case 'error':
        return <AlertOctagon size={20} color="#ef4444" />;
      case 'warning':
        return <AlertTriangle size={20} color="#f59e0b" />;
      case 'security':
        return <ShieldAlert size={22} color="#a855f7" />;
      default:
        return <Info size={20} color="#3b82f6" />;
    }
  };

  return (
    <div className="toasts-portal">
      {toasts.map(toast => (
        <div key={toast.id} className={`toast-item toast-${toast.type}`}>
          <div style={{ flexShrink: 0, marginTop: '2px' }}>
            {getIcon(toast.type)}
          </div>
          <div style={{ flex: 1 }}>
            <div className="toast-title">{toast.title}</div>
            <div className="toast-message">{toast.message}</div>
          </div>
        </div>
      ))}
    </div>
  );
};
