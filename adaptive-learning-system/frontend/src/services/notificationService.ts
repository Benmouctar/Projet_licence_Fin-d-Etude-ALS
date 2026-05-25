import api from './api';
import type { NotificationDTO } from '../types';

/**
 * API service layer handling asynchronous network requests to backend endpoints for notification.
 */
export const notificationService = {
  
  getMyNotifications: (): Promise<NotificationDTO[]> =>
    api.get<NotificationDTO[]>('/notifications/my').then(r => r.data),

  
  markRead: (id: string): Promise<void> =>
    api.put(`/notifications/${id}/read`).then(() => undefined),
};
