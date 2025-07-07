-- Create tasks table
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM',
    assignee_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    due_date TIMESTAMP NULL
);

-- Create indexes for better performance
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_assignee_id ON tasks(assignee_id);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);

-- Insert some sample data
INSERT INTO tasks (title, description, status, priority, assignee_id) VALUES
('Setup development environment', 'Configure IDE and development tools', 'COMPLETED', 'HIGH', 'user123'),
('Write API documentation', 'Create comprehensive API documentation', 'IN_PROGRESS', 'MEDIUM', 'user123'),
('Implement user authentication', 'Add user login and registration', 'PENDING', 'HIGH', 'user456'),
('Design database schema', 'Create entity relationship diagrams', 'COMPLETED', 'MEDIUM', 'user789'),
('Setup CI/CD pipeline', 'Configure automated testing and deployment', 'PENDING', 'LOW', 'user456');