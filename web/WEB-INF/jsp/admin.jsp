<%@page import="kitaran.bean.User"%>
<%@page import="kitaran.bean.AdminDTO"%>
<%@page import="java.util.ArrayList"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="${pageContext.request.contextPath}/globals.css" rel="stylesheet">
    <title>Admin Dashboard - Kitaran</title>
    <style>
        .stats-banner {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 24px;
            margin-bottom: 32px;
        }

        .stat-item {
            background: var(--bg-primary);
            border: 1px solid var(--neutral-200);
            border-radius: var(--radius-lg);
            padding: 24px;
            display: flex;
            align-items: center;
            gap: 20px;
            transition: all var(--transition-base);
        }

        .stat-item:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-lg);
            border-color: var(--primary-light);
        }

        .stat-icon {
            width: 64px;
            height: 64px;
            border-radius: var(--radius-md);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 28px;
            flex-shrink: 0;
        }

        .stat-icon.primary {
            background: linear-gradient(135deg, rgba(123, 166, 130, 0.15) 0%, rgba(123, 166, 130, 0.05) 100%);
            color: var(--primary);
        }

        .stat-icon.info {
            background: linear-gradient(135deg, rgba(33, 150, 243, 0.15) 0%, rgba(33, 150, 243, 0.05) 100%);
            color: var(--info);
        }

        .stat-icon.warning {
            background: linear-gradient(135deg, rgba(255, 152, 0, 0.15) 0%, rgba(255, 152, 0, 0.05) 100%);
            color: var(--warning);
        }

        .stat-content {
            flex: 1;
        }

        .stat-label {
            font-size: 13px;
            font-weight: 700;
            color: var(--neutral-500);
            text-transform: uppercase;
            letter-spacing: 0.5px;
            margin-bottom: 6px;
        }

        .stat-value {
            font-size: 32px;
            font-weight: 800;
            color: var(--neutral-900);
            line-height: 1;
        }

        /* Action buttons in table */
        .btn-verify {
            background: var(--primary);
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: var(--radius-md);
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
            transition: all var(--transition-base);
            display: inline-flex;
            align-items: center;
            gap: 6px;
        }

        .btn-verify:hover {
            background: var(--primary-dark);
            transform: translateY(-2px);
            box-shadow: var(--shadow-md);
        }

        .btn-penalty {
            background: rgba(229, 57, 53, 0.1);
            color: var(--danger);
            border: 1px solid rgba(229, 57, 53, 0.2);
            padding: 6px 12px;
            border-radius: var(--radius-md);
            font-weight: 700;
            font-size: 12px;
            cursor: pointer;
            transition: all var(--transition-base);
        }

        .btn-penalty:hover {
            background: var(--danger);
            color: white;
            border-color: var(--danger);
        }

        .btn-disabled {
            background: var(--neutral-200);
            color: var(--neutral-400);
            cursor: not-allowed;
            opacity: 0.6;
        }

        .btn-disabled:hover {
            transform: none;
            box-shadow: none;
        }

        .action-completed {
            color: var(--success);
            font-weight: 700;
            font-size: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
        }

        .modal {
            display: none;
            position: fixed;
            inset: 0;
            background: rgba(26, 35, 30, 0.6);
            backdrop-filter: blur(4px);
            z-index: 9999;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        .modal-content {
            background: white;
            padding: 40px;
            border-radius: var(--radius-xl);
            width: 100%;
            max-width: 440px;
            box-shadow: var(--shadow-xl);
            animation: modalSlideIn 0.3s ease;
        }

        @keyframes modalSlideIn {
            from {
                transform: translateY(-50px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }

        .modal-content h3 {
            font-size: 24px;
            font-weight: 800;
            color: var(--neutral-900);
            margin-bottom: 12px;
        }

        .modal-content p {
            color: var(--neutral-500);
            font-size: 14px;
            margin-bottom: 24px;
        }

        .modal-content input {
            margin-bottom: 24px;
        }

        .modal-content .btn {
            width: 100%;
            margin-bottom: 12px;
        }

        .modal-content .btn-cancel {
            background: transparent;
            color: var(--neutral-600);
            border: 1px solid var(--neutral-300);
        }

        .modal-content .btn-cancel:hover {
            background: var(--neutral-100);
            color: var(--neutral-800);
        }
    </style>
</head>
<body>
    <div class="app-container">
        <!-- Sidebar -->
        <aside class="sidebar">
            <div class="logo">Ki<span>TARAN</span></div>
            <nav class="nav-links">
                <ul>
                    <li>
                        <a href="dashboard" class="active">
                            <i class="fas fa-tachometer-alt"></i>
                            <span>Admin Panel</span>
                        </a>
                    </li>
                    <li class="spacer"></li>
                    <li>
                        <form action="logout" method="post">
                            <button type="submit" class="logout">
                                <i class="fas fa-sign-out-alt"></i>
                                <span>Logout</span>
                            </button>
                        </form>
                    </li>
                </ul>
            </nav>
        </aside>

        <!-- Main Content -->
        <main class="main-content">
            <!-- Header -->
            <div class="welcome-section">
                <h2><i class="fas fa-shield-alt" style="color: var(--primary);"></i> Admin Command Center</h2>
                <p>System management and oversight dashboard</p>
            </div>

            <!-- Statistics Banner -->
            <div class="stats-banner">
                <div class="stat-item">
                    <div class="stat-icon primary">
                        <i class="fas fa-weight"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Total Weight</div>
                        <div class="stat-value">${totalSystemWeight != null ? totalSystemWeight : '0.00'} <small style="font-size: 16px; color: var(--neutral-500);">kg</small></div>
                    </div>
                </div>

                <div class="stat-item">
                    <div class="stat-icon info">
                        <i class="fas fa-clipboard-list"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Total Requests</div>
                        <div class="stat-value">${totalRequests != null ? totalRequests : '0'}</div>
                    </div>
                </div>

                <div class="stat-item">
                    <div class="stat-icon warning">
                        <i class="fas fa-hourglass-half"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Pending</div>
                        <div class="stat-value">${pendingRequests != null ? pendingRequests : '0'}</div>
                    </div>
                </div>
            </div>

            <!-- Requests Table -->
            <div class="box">
                <h3><i class="fas fa-list"></i> Recycling Requests</h3>
                <table>
                    <thead>
                        <tr>
                            <th class="text-center"><i class="fas fa-hashtag"></i> ID</th>
                            <th><i class="fas fa-user"></i> Contributor</th>
                            <th class="text-center"><i class="fas fa-weight-hanging"></i> Weight</th>
                            <th class="text-center"><i class="fas fa-dollar-sign"></i> Penalty</th>
                            <th class="text-center"><i class="fas fa-info-circle"></i> Status</th>
                            <th class="text-center"><i class="fas fa-cog"></i> Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="record" items="${data}">
                            <tr>
                                <td class="text-center" style="font-weight: 800; color: var(--primary);">
                                    #${record.recycleId}
                                </td>
                                <td>
                                    <div style="font-weight: 700; color: var(--neutral-800);">${record.username}</div>
                                    <div style="font-size: 12px; color: var(--neutral-400);">UID: ${record.userId}</div>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${record.weight > 0}">
                                            <span style="font-weight: 700; color: var(--primary);">
                                                <fmt:formatNumber value="${record.weight}" maxFractionDigits="2"/> kg
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <button onclick="openWeightModal(${record.recycleId})" class="btn-verify btn-small">
                                                <i class="fas fa-pen"></i> Input
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${record.penaltyAmount > 0}">
                                            <span style="color: var(--danger); font-weight: 800;">
                                                <i class="fas fa-exclamation-triangle"></i>
                                                RM <fmt:formatNumber value="${record.penaltyAmount}" minFractionDigits="2"/>
                                            </span>
                                        </c:when>
                                        <c:when test="${record.recycleStatus == 'verified'}">
                                            <span style="color: var(--success);">
                                                <i class="fas fa-check"></i>
                                            </span>
                                        </c:when>
                                        <c:when test="${record.weight <= 0}">
                                            <span style="font-size: 12px; color: var(--neutral-400);">
                                                <i class="fas fa-lock"></i> Locked
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <button onclick="openPenaltyModal(${record.userId}, ${record.recycleId})" class="btn-penalty">
                                                <i class="fas fa-plus"></i> Add
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <span class="status-badge ${record.recycleStatus == 'pending' ? 'status-pending' : 'status-verified'}">
                                        <i class="fas ${record.recycleStatus == 'pending' ? 'fa-hourglass-half' : 'fa-check-circle'}"></i>
                                        ${record.recycleStatus}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${record.recycleStatus == 'verified'}">
                                            <div class="action-completed">
                                                <i class="fas fa-check-circle"></i> Completed
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <c:choose>
                                                <c:when test="${record.weight > 0}">
                                                    <form action="dashboard" method="POST" style="display: inline;">
                                                        <input type="hidden" name="action" value="verify">
                                                        <input type="hidden" name="recycleId" value="${record.recycleId}">
                                                        <button type="submit" class="btn-verify" onclick="return confirm('Verify this request?')">
                                                            <i class="fas fa-check"></i> Verify
                                                        </button>
                                                    </form>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="btn-verify btn-disabled" disabled title="Input weight first">
                                                        <i class="fas fa-lock"></i> Verify
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </main>
    </div>

    <!-- Weight Input Modal -->
    <div id="weightModal" class="modal">
        <div class="modal-content">
            <h3><i class="fas fa-weight" style="color: var(--primary);"></i> Input Weight</h3>
            <p>Enter the verified weight for this recycling request</p>
            <form action="dashboard" method="POST">
                <input type="hidden" name="action" value="weight">
                <input type="hidden" id="recycleId2" name="requestId">
                <input type="number" name="weight" placeholder="Weight in kg (e.g., 5.5)" step="0.1" min="0.1" required>
                <button type="submit" class="btn">
                    <i class="fas fa-save"></i> Save Weight
                </button>
                <button type="button" onclick="closeWeightModal()" class="btn btn-cancel">
                    <i class="fas fa-times"></i> Cancel
                </button>
            </form>
        </div>
    </div>

    <!-- Penalty Modal -->
    <div id="penaltyModal" class="modal">
        <div class="modal-content">
            <h3><i class="fas fa-exclamation-triangle" style="color: var(--danger);"></i> Apply Penalty</h3>
            <p>Rate: <strong>RM 0.50 per kg</strong> for incorrect items</p>
            <form action="dashboard" method="POST">
                <input type="hidden" name="action" value="penalty">
                <input type="hidden" id="userId" name="userId">
                <input type="hidden" id="recycleId" name="recycleId">
                <input type="number" name="wrongWeight" placeholder="Incorrect weight in kg" step="0.1" min="0.1" required>
                <button type="submit" class="btn btn-red">
                    <i class="fas fa-exclamation-circle"></i> Apply Penalty
                </button>
                <button type="button" onclick="closePenaltyModal()" class="btn btn-cancel">
                    <i class="fas fa-times"></i> Cancel
                </button>
            </form>
        </div>
    </div>

    <script>
        function openWeightModal(recycleId) {
            document.getElementById('recycleId2').value = recycleId;
            document.getElementById('weightModal').style.display = 'flex';
        }

        function closeWeightModal() {
            document.getElementById('weightModal').style.display = 'none';
        }

        function openPenaltyModal(userId, recycleId) {
            document.getElementById('userId').value = userId;
            document.getElementById('recycleId').value = recycleId;
            document.getElementById('penaltyModal').style.display = 'flex';
        }

        function closePenaltyModal() {
            document.getElementById('penaltyModal').style.display = 'none';
        }

        window.onclick = function(event) {
            const weightModal = document.getElementById('weightModal');
            const penaltyModal = document.getElementById('penaltyModal');
            if (event.target == weightModal) {
                closeWeightModal();
            }
            if (event.target == penaltyModal) {
                closePenaltyModal();
            }
        }
    </script>
</body>
</html>