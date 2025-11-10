import React, { useState } from 'react';

const OTPModal = ({ onVerify, onClose, generatedOTP }) => {
  const [otp, setOtp] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!otp || otp.length !== 6) {
      setError('OTP must be 6 digits');
      return;
    }
    
    if (!/^\d{6}$/.test(otp)) {
      setError('OTP must contain only numbers');
      return;
    }

    setIsSubmitting(true);
    setError('');
    
    try {
      await onVerify(otp);
    } catch (error) {
      setError('Verification failed. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOtpChange = (e) => {
    const value = e.target.value.replace(/\D/g, '').slice(0, 6);
    setOtp(value);
    setError('');
  };

  return (
    <div className="modal">
      <div className="modal-content">
        <h3>OTP Verification</h3>
        <p>Please enter the 6-digit OTP to verify your claim submission.</p>
        <p style={{ fontSize: '12px', color: '#666' }}>
          For demo purposes, your OTP is: <strong>{generatedOTP}</strong>
        </p>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <input
              type="text"
              value={otp}
              onChange={handleOtpChange}
              placeholder="Enter 6-digit OTP"
              className="otp-input"
              maxLength="6"
            />
            {error && <div className="error-message">{error}</div>}
          </div>
          
          <div className="modal-buttons">
            <button 
              type="submit" 
              className="btn-primary" 
              disabled={isSubmitting || otp.length !== 6}
            >
              {isSubmitting ? 'Verifying...' : 'Verify OTP'}
            </button>
            <button 
              type="button" 
              className="btn-secondary" 
              onClick={onClose}
              disabled={isSubmitting}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default OTPModal;