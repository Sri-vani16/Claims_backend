import React from 'react';

const ClaimResult = ({ result }) => {
  if (!result) {
    return (
      <div className="result-container">
        <h3>❌ Error: No result data available</h3>
      </div>
    );
  }

  const getFraudScoreClass = (score) => {
    const numScore = Number(score) || 0;
    if (numScore >= 75) return 'high';
    if (numScore >= 40) return 'medium';
    return 'low';
  };

  const getFraudScoreText = (score) => {
    const numScore = Number(score) || 0;
    if (numScore >= 75) return 'High Risk';
    if (numScore >= 40) return 'Medium Risk';
    return 'Low Risk';
  };

  return (
    <div className="result-container">
      <h3>✅ Claim Submitted Successfully!</h3>
      
      <div style={{ marginBottom: '20px' }}>
        <h4>Claim Reference Number:</h4>
        <div style={{ 
          fontSize: '24px', 
          fontWeight: 'bold', 
          color: '#007bff',
          padding: '10px',
          background: '#e7f3ff',
          borderRadius: '4px',
          textAlign: 'center'
        }}>
          {result.claimReferenceNumber || 'N/A'}
        </div>
      </div>

      <div className={`fraud-score ${getFraudScoreClass(result.fraudScore)}`}>
        Fraud Risk Score: {result.fraudScore || 0}/100 ({getFraudScoreText(result.fraudScore)})
      </div>

      {result.fraudFlags && result.fraudFlags.length > 0 && (
        <div className="fraud-flags">
          <h4>Fraud Detection Alerts:</h4>
          {result.fraudFlags.map((flag, index) => (
            <div 
              key={index} 
              className={`fraud-flag ${result.fraudScore >= 75 ? 'high' : ''}`}
            >
              {flag}
            </div>
          ))}
        </div>
      )}

      {result.fraudScore >= 75 && (
        <div style={{ 
          background: '#f8d7da', 
          border: '1px solid #f5c6cb', 
          color: '#721c24',
          padding: '15px',
          borderRadius: '4px',
          marginTop: '20px'
        }}>
          <strong>⚠️ High Risk Alert:</strong> This claim has been flagged for manual review. 
          Our fraud investigation team has been notified and will contact you within 24 hours.
        </div>
      )}

      <div style={{ marginTop: '30px', textAlign: 'center' }}>
        <button 
          className="btn-primary" 
          onClick={() => window.location.reload()}
          style={{ padding: '12px 30px', fontSize: '16px' }}
        >
          Submit Another Claim
        </button>
      </div>

      <div style={{ 
        marginTop: '20px', 
        padding: '15px', 
        background: '#d4edda', 
        border: '1px solid #c3e6cb',
        borderRadius: '4px',
        fontSize: '14px'
      }}>
        <strong>Next Steps:</strong>
        <ul style={{ textAlign: 'left', marginTop: '10px' }}>
          <li>Save your claim reference number for future correspondence</li>
          <li>You will receive an email confirmation within 24 hours</li>
          <li>Our claims team will contact you within 2-3 business days</li>
          <li>Keep all relevant documents and receipts ready</li>
        </ul>
      </div>
    </div>
  );
};

export default ClaimResult;