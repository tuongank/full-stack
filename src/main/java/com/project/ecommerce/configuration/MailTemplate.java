package com.project.ecommerce.configuration;

import com.project.ecommerce.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Component
public class MailTemplate {

        // ─────────────────────────────────────────────────────────────
        // Shared helpers
        // ─────────────────────────────────────────────────────────────

        private String formatVnd(BigDecimal amount) {
                NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                return nf.format(amount) + " ₫";
        }

        /** Common HTML wrapper used by all email templates */
        private String wrap(String title, String bodyContent) {
                return """
                                <!DOCTYPE html>
                                <html lang="vi">
                                <head>
                                  <meta charset="UTF-8"/>
                                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                                  <title>%s</title>
                                </head>
                                <body style="margin:0;padding:0;background:#f0f4ff;font-family:'Segoe UI',Arial,sans-serif;">
                                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f0f4ff;padding:40px 0;">
                                    <tr><td align="center">
                                      <table width="600" cellpadding="0" cellspacing="0"
                                             style="background:#ffffff;border-radius:16px;overflow:hidden;
                                                    box-shadow:0 4px 24px rgba(99,102,241,.12);max-width:600px;width:100%%;">

                                        <!-- Header -->
                                        <tr>
                                          <td style="background:linear-gradient(135deg,#6366f1 0%%,#8b5cf6 50%%,#c084fc 100%%);
                                                     padding:36px 40px;text-align:center;">
                                            <p style="margin:0;font-size:28px;font-weight:800;color:#ffffff;letter-spacing:-0.5px;">
                                              💜 BeautyMed
                                            </p>
                                            <p style="margin:8px 0 0;font-size:13px;color:rgba(255,255,255,.75);letter-spacing:1px;">
                                              CHĂM SÓC DA THÔNG MINH
                                            </p>
                                          </td>
                                        </tr>

                                        <!-- Body -->
                                        <tr>
                                          <td style="padding:36px 40px;">
                                            %s
                                          </td>
                                        </tr>

                                        <!-- Footer -->
                                        <tr>
                                          <td style="background:#f8f9ff;padding:24px 40px;text-align:center;
                                                     border-top:1px solid #e8edff;">
                                            <p style="margin:0;font-size:12px;color:#94a3b8;">
                                              © 2026 BeautyMed · Hệ thống thương mại điện tử chăm sóc da thông minh
                                            </p>
                                            <p style="margin:6px 0 0;font-size:12px;color:#94a3b8;">
                                              Vui lòng không trả lời email này. Nếu cần hỗ trợ, liên hệ
                                              <a href="mailto:support@beautymed.vn"
                                                 style="color:#6366f1;text-decoration:none;">support@beautymed.vn</a>
                                            </p>
                                          </td>
                                        </tr>

                                      </table>
                                    </td></tr>
                                  </table>
                                </body>
                                </html>
                                """
                                .formatted(title, bodyContent);
        }

        // ─────────────────────────────────────────────────────────────
        // Verification Code
        // ─────────────────────────────────────────────────────────────

        public String verificationCode(String code, long minutes) {
                String body = """
                                <h2 style="margin:0 0 8px;font-size:22px;font-weight:700;color:#1e293b;">
                                  Xác thực tài khoản của bạn
                                </h2>
                                <p style="margin:0 0 28px;font-size:15px;color:#64748b;line-height:1.6;">
                                  Chào mừng bạn đến với BeautyMed! Vui lòng sử dụng mã xác thực bên dưới
                                  để hoàn tất đăng ký tài khoản.
                                </p>

                                <!-- OTP Box -->
                                <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:28px;">
                                  <tr><td align="center">
                                    <div style="display:inline-block;background:linear-gradient(135deg,#6366f1,#8b5cf6);
                                                border-radius:14px;padding:2px;">
                                      <div style="background:#fff;border-radius:12px;padding:20px 48px;">
                                        <p style="margin:0;font-size:38px;font-weight:900;
                                                   letter-spacing:12px;color:#6366f1;font-family:monospace;">
                                          %s
                                        </p>
                                      </div>
                                    </div>
                                  </td></tr>
                                </table>

                                <!-- Warning -->
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="background:#fff7ed;border-radius:10px;border-left:4px solid #f97316;
                                              margin-bottom:24px;">
                                  <tr><td style="padding:14px 18px;">
                                    <p style="margin:0;font-size:14px;color:#9a3412;">
                                      ⏰ Mã sẽ hết hạn sau <strong>%d phút</strong>.
                                      Vui lòng không chia sẻ mã này với bất kỳ ai.
                                    </p>
                                  </td></tr>
                                </table>

                                <p style="margin:0;font-size:13px;color:#94a3b8;line-height:1.6;">
                                  Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email.
                                </p>
                                """.formatted(code, minutes);

                return wrap("Xác thực tài khoản BeautyMed", body);
        }

        // ─────────────────────────────────────────────────────────────
        // Forgot Password
        // ─────────────────────────────────────────────────────────────

        public String forgotPassword(String code, long minutes) {
                String body = """
                                <h2 style="margin:0 0 8px;font-size:22px;font-weight:700;color:#1e293b;">
                                  Đặt lại mật khẩu
                                </h2>
                                <p style="margin:0 0 28px;font-size:15px;color:#64748b;line-height:1.6;">
                                  Chúng tôi nhận được yêu cầu đặt lại mật khẩu từ tài khoản của bạn.
                                  Sử dụng mã bên dưới để tiếp tục.
                                </p>

                                <!-- OTP Box -->
                                <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:28px;">
                                  <tr><td align="center">
                                    <div style="display:inline-block;background:linear-gradient(135deg,#f97316,#ef4444);
                                                border-radius:14px;padding:2px;">
                                      <div style="background:#fff;border-radius:12px;padding:20px 48px;">
                                        <p style="margin:0;font-size:38px;font-weight:900;
                                                   letter-spacing:12px;color:#ef4444;font-family:monospace;">
                                          %s
                                        </p>
                                      </div>
                                    </div>
                                  </td></tr>
                                </table>

                                <!-- Warning -->
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="background:#fff7ed;border-radius:10px;border-left:4px solid #f97316;
                                              margin-bottom:24px;">
                                  <tr><td style="padding:14px 18px;">
                                    <p style="margin:0;font-size:14px;color:#9a3412;">
                                      ⏰ Mã sẽ hết hạn sau <strong>%d phút</strong>.
                                      Tuyệt đối không chia sẻ mã này với bất kỳ ai.
                                    </p>
                                  </td></tr>
                                </table>

                                <!-- Security Note -->
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="background:#fef2f2;border-radius:10px;border-left:4px solid #ef4444;">
                                  <tr><td style="padding:14px 18px;">
                                    <p style="margin:0;font-size:13px;color:#7f1d1d;">
                                      🔒 Nếu bạn không yêu cầu đặt lại mật khẩu, tài khoản của bạn có thể
                                      đang bị truy cập trái phép. Hãy liên hệ hỗ trợ ngay lập tức.
                                    </p>
                                  </td></tr>
                                </table>
                                """.formatted(code, minutes);

                return wrap("Đặt lại mật khẩu BeautyMed", body);
        }

        // ─────────────────────────────────────────────────────────────
        // Order Success
        // ─────────────────────────────────────────────────────────────

        public String orderSuccess(String customerName,
                        List<OrderItem> items,
                        BigDecimal total) {

                // Build product rows
                StringBuilder rows = new StringBuilder();
                for (OrderItem item : items) {
                        BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        rows.append("""
                                        <tr>
                                          <td style="padding:12px 16px;border-bottom:1px solid #e8edff;
                                                     font-size:14px;color:#1e293b;">
                                            %s
                                          </td>
                                          <td style="padding:12px 16px;border-bottom:1px solid #e8edff;
                                                     font-size:14px;color:#64748b;text-align:center;">
                                            x%d
                                          </td>
                                          <td style="padding:12px 16px;border-bottom:1px solid #e8edff;
                                                     font-size:14px;color:#6366f1;text-align:right;font-weight:600;">
                                            %s
                                          </td>
                                        </tr>
                                        """.formatted(
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        formatVnd(lineTotal)));
                }

                String body = """
                                <h2 style="margin:0 0 4px;font-size:22px;font-weight:700;color:#1e293b;">
                                  Đặt hàng thành công! 🎉
                                </h2>
                                <p style="margin:0 0 24px;font-size:15px;color:#64748b;line-height:1.6;">
                                  Xin chào <strong>%s</strong>, đơn hàng của bạn đã được xác nhận.
                                  Chúng tôi sẽ xử lý và giao hàng sớm nhất có thể!
                                </p>

                                <!-- Status badge -->
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="background:linear-gradient(135deg,#ecfdf5,#d1fae5);
                                              border-radius:12px;border-left:4px solid #10b981;margin-bottom:28px;">
                                  <tr><td style="padding:16px 20px;">
                                    <p style="margin:0;font-size:15px;color:#065f46;font-weight:600;">
                                      ✅ Trạng thái: <span style="color:#10b981;">Đang xử lý</span>
                                    </p>
                                  </td></tr>
                                </table>

                                <!-- Items table -->
                                <p style="margin:0 0 12px;font-size:14px;font-weight:700;color:#475569;
                                          text-transform:uppercase;letter-spacing:.5px;">
                                  Sản phẩm đã đặt
                                </p>
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="border:1px solid #e8edff;border-radius:12px;
                                              overflow:hidden;margin-bottom:20px;">
                                  <thead>
                                    <tr style="background:#f8f9ff;">
                                      <th style="padding:11px 16px;font-size:13px;color:#64748b;
                                                 text-align:left;font-weight:600;border-bottom:1px solid #e8edff;">
                                        Sản phẩm
                                      </th>
                                      <th style="padding:11px 16px;font-size:13px;color:#64748b;
                                                 text-align:center;font-weight:600;border-bottom:1px solid #e8edff;">
                                        SL
                                      </th>
                                      <th style="padding:11px 16px;font-size:13px;color:#64748b;
                                                 text-align:right;font-weight:600;border-bottom:1px solid #e8edff;">
                                        Thành tiền
                                      </th>
                                    </tr>
                                  </thead>
                                  <tbody>
                                    %s
                                  </tbody>
                                </table>

                                <!-- Total -->
                                <table width="100%%" cellpadding="0" cellspacing="0"
                                       style="background:linear-gradient(135deg,#6366f1,#8b5cf6);
                                              border-radius:12px;margin-bottom:28px;">
                                  <tr>
                                    <td style="padding:18px 20px;">
                                      <table width="100%%"><tr>
                                        <td style="font-size:16px;font-weight:700;color:#fff;">Tổng thanh toán</td>
                                        <td align="right" style="font-size:22px;font-weight:900;color:#fff;">%s</td>
                                      </tr></table>
                                    </td>
                                  </tr>
                                </table>

                                <!-- CTA -->
                                <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:24px;">
                                  <tr><td align="center">
                                    <a href="http://localhost:5173/orders"
                                       style="display:inline-block;background:linear-gradient(135deg,#6366f1,#8b5cf6);
                                              color:#fff;text-decoration:none;padding:14px 36px;border-radius:10px;
                                              font-size:15px;font-weight:700;
                                              box-shadow:0 4px 16px rgba(99,102,241,.35);">
                                      Xem đơn hàng của tôi →
                                    </a>
                                  </td></tr>
                                </table>

                                <p style="margin:0;font-size:13px;color:#94a3b8;text-align:center;line-height:1.6;">
                                  Cảm ơn bạn đã tin tưởng mua sắm tại BeautyMed! 💜
                                </p>
                                """.formatted(customerName, rows.toString(), formatVnd(total));

                return wrap("Xác nhận đơn hàng - BeautyMed", body);
        }
}
