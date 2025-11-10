import React, { useState } from 'react';
import { useClaim } from '../context/ClaimContext';
import { claimAPI } from '../services/api';
import { validationRules, validateField } from '../utils/validation';
import OTPModal from './OTPModal';
import ClaimResult from './ClaimResult';

const SubmitClaim = () => {
  const { dispatch, loading, submissionResult } = useClaim();
  const [formData, setFormData] = useState({
    policyNumber: '',
    claimType: '',
    natureOfClaim: '',
    dateOfIncident: '',
    timeOfIncident: '',
    locationOfIncident: '',
    descriptionOfIncident: '',
    wasThirdPartyInvolved: null,
    policeReportFiled: null,
    policeReportNumber: '',
    policeStationName: '',
    isClaimantPolicyholder: null,
    claimantName: '',
    relationship: '',
    phone: '',
    email: '',
    claimAmount: '',
    injuriesInvolved: null,
    injuryType: '',
    injurySeverity: '',
    declarationAccepted: false,
    otp: ''
  });

  const [errors, setErrors] = useState({});
  const [showOTPModal, setShowOTPModal] = useState(false);
  const [generatedOTP, setGeneratedOTP] = useState('');

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    const newValue = type === 'checkbox' ? checked : value;
    
    setFormData(prev => ({
      ...prev,
      [name]: newValue
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: null
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};
    
    console.log('Starting form validation...');
    console.log('Current form data:', formData);
    
    // Validate all fields except OTP (OTP is validated separately in the modal)
    Object.keys(validationRules).forEach(field => {
      if (field === 'otp') return; // Skip OTP validation during form submission
      
      const fieldValue = formData[field];
      const error = validateField(field, fieldValue, validationRules);
      console.log(`Field ${field}: value="${fieldValue}", error="${error}"`);
      if (error) {
        newErrors[field] = error;
      }
    });

    // Cross-field validations
    if (formData.wasThirdPartyInvolved === 'true' && (!formData.claimantName || !formData.phone)) {
      newErrors.claimantName = 'Third party details are required';
      console.log('Cross-validation failed: Third party details required');
    }

    if (formData.policeReportFiled === 'true' && (!formData.policeReportNumber || !formData.policeStationName)) {
      newErrors.policeReportNumber = 'Police report details are required';
      newErrors.policeStationName = 'Police station name is required';
      console.log('Cross-validation failed: Police report details required');
    }

    if (formData.isClaimantPolicyholder === 'false' && (!formData.claimantName || !formData.relationship)) {
      newErrors.claimantName = 'Claimant details are required';
      newErrors.relationship = 'Relationship is required';
      console.log('Cross-validation failed: Claimant details required');
    }

    console.log('Final validation errors:', newErrors);
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    console.log('Form submitted with data:', formData);
    
    try {
      const isValid = validateForm();
      console.log('Form validation result:', isValid);
      console.log('Validation errors:', errors);
      
      if (!isValid) {
        console.log('Form validation failed, not proceeding');
        return;
      }

      console.log('Form is valid, generating OTP');
      // Generate and show OTP
      const otp = Math.floor(100000 + Math.random() * 900000).toString();
      setGeneratedOTP(otp);
      setShowOTPModal(true);
    } catch (error) {
      console.error('Form submission error:', error);
      alert('An error occurred while processing your request. Please try again.');
    }
  };

  const handleOTPVerification = async (enteredOTP) => {
    console.log('OTP verification started:', enteredOTP, 'Expected:', generatedOTP);
    
    if (!enteredOTP || enteredOTP !== generatedOTP) {
      alert('Invalid OTP. Please try again.');
      return;
    }

    setShowOTPModal(false);
    dispatch({ type: 'SET_LOADING', payload: true });

    try {
      const claimAmount = parseFloat(formData.claimAmount);
      if (isNaN(claimAmount) || claimAmount <= 0) {
        throw new Error('Invalid claim amount');
      }

      const claimData = {
        ...formData,
        otp: enteredOTP,
        wasThirdPartyInvolved: formData.wasThirdPartyInvolved === 'true',
        policeReportFiled: formData.policeReportFiled === 'true',
        isClaimantPolicyholder: formData.isClaimantPolicyholder === 'true',
        claimAmount
      };

      console.log('Sending claim data to API:', claimData);
      const result = await claimAPI.submitClaim(claimData);
      console.log('API response:', result);
      dispatch({ type: 'SET_SUBMISSION_RESULT', payload: result });
    } catch (error) {
      console.error('Claim submission error:', error);
      dispatch({ type: 'SET_ERROR', payload: error.message || 'Failed to submit claim' });
    } finally {
      dispatch({ type: 'SET_LOADING', payload: false });
    }
  };

  if (submissionResult) {
    return <ClaimResult result={submissionResult} />;
  }

  return (
    <div className="claim-form">
      <h2>Submit Insurance Claim</h2>
      
      <form onSubmit={handleSubmit}>
        {/* Policy Selection */}
        <div className="form-section">
          <h3>Policy Information</h3>
          <div className="form-row">
            <div className={`form-group ${errors.policyNumber ? 'error' : ''}`}>
              <label>Policy Number *</label>
              <input
                type="text"
                name="policyNumber"
                value={formData.policyNumber}
                onChange={handleInputChange}
                placeholder="POL12345678 or INS-123456789"
              />
              {errors.policyNumber && <div className="error-message">{errors.policyNumber}</div>}
            </div>
            <div className={`form-group ${errors.claimType ? 'error' : ''}`}>
              <label>Claim Type *</label>
              <select name="claimType" value={formData.claimType} onChange={handleInputChange}>
                <option value="">Select Claim Type</option>
                <option value="Medical">Medical</option>
                <option value="Auto">Auto</option>
                <option value="Home">Home</option>
                <option value="Life">Life</option>
                <option value="Travel">Travel</option>
              </select>
              {errors.claimType && <div className="error-message">{errors.claimType}</div>}
            </div>
          </div>
          <div className={`form-group ${errors.natureOfClaim ? 'error' : ''}`}>
            <label>Nature of Claim *</label>
            <select name="natureOfClaim" value={formData.natureOfClaim} onChange={handleInputChange}>
              <option value="">Select Nature of Claim</option>
              <option value="Accident">Accident</option>
              <option value="Theft">Theft</option>
              <option value="Fire">Fire</option>
              <option value="Flood">Flood</option>
              <option value="Medical">Medical</option>
              <option value="Natural Disaster">Natural Disaster</option>
              <option value="Other">Other</option>
            </select>
            {errors.natureOfClaim && <div className="error-message">{errors.natureOfClaim}</div>}
          </div>
        </div>

        {/* Incident Information */}
        <div className="form-section">
          <h3>Incident Information</h3>
          <div className="form-row">
            <div className={`form-group ${errors.dateOfIncident ? 'error' : ''}`}>
              <label>Date of Incident *</label>
              <input
                type="date"
                name="dateOfIncident"
                value={formData.dateOfIncident}
                onChange={handleInputChange}
              />
              {errors.dateOfIncident && <div className="error-message">{errors.dateOfIncident}</div>}
            </div>
            <div className="form-group">
              <label>Time of Incident</label>
              <input
                type="time"
                name="timeOfIncident"
                value={formData.timeOfIncident}
                onChange={handleInputChange}
              />
            </div>
          </div>
          <div className={`form-group ${errors.locationOfIncident ? 'error' : ''}`}>
            <label>Location of Incident *</label>
            <input
              type="text"
              name="locationOfIncident"
              value={formData.locationOfIncident}
              onChange={handleInputChange}
              placeholder="City, State"
            />
            {errors.locationOfIncident && <div className="error-message">{errors.locationOfIncident}</div>}
          </div>
          <div className={`form-group ${errors.descriptionOfIncident ? 'error' : ''}`}>
            <label>Description of Incident * (15-500 characters)</label>
            <textarea
              name="descriptionOfIncident"
              value={formData.descriptionOfIncident}
              onChange={handleInputChange}
              rows="4"
              placeholder="Describe what happened..."
            />
            <div style={{fontSize: '12px', color: '#666'}}>
              {formData.descriptionOfIncident.length}/500 characters
            </div>
            {errors.descriptionOfIncident && <div className="error-message">{errors.descriptionOfIncident}</div>}
          </div>
          
          <div className="form-group">
            <label>Was Third Party Involved? *</label>
            <div className="radio-group">
              <label>
                <input
                  type="radio"
                  name="wasThirdPartyInvolved"
                  value="true"
                  checked={formData.wasThirdPartyInvolved === 'true'}
                  onChange={handleInputChange}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="wasThirdPartyInvolved"
                  value="false"
                  checked={formData.wasThirdPartyInvolved === 'false'}
                  onChange={handleInputChange}
                />
                No
              </label>
            </div>
          </div>

          <div className="form-group">
            <label>Police Report Filed? *</label>
            <div className="radio-group">
              <label>
                <input
                  type="radio"
                  name="policeReportFiled"
                  value="true"
                  checked={formData.policeReportFiled === 'true'}
                  onChange={handleInputChange}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="policeReportFiled"
                  value="false"
                  checked={formData.policeReportFiled === 'false'}
                  onChange={handleInputChange}
                />
                No
              </label>
            </div>
          </div>

          {formData.policeReportFiled === 'true' && (
            <div className="form-row">
              <div className={`form-group ${errors.policeReportNumber ? 'error' : ''}`}>
                <label>Police Report Number *</label>
                <input
                  type="text"
                  name="policeReportNumber"
                  value={formData.policeReportNumber}
                  onChange={handleInputChange}
                />
                {errors.policeReportNumber && <div className="error-message">{errors.policeReportNumber}</div>}
              </div>
              <div className={`form-group ${errors.policeStationName ? 'error' : ''}`}>
                <label>Police Station Name *</label>
                <input
                  type="text"
                  name="policeStationName"
                  value={formData.policeStationName}
                  onChange={handleInputChange}
                />
                {errors.policeStationName && <div className="error-message">{errors.policeStationName}</div>}
              </div>
            </div>
          )}

          <div className="form-group">
            <label>Any injuries/deaths involved? *</label>
            <div className="radio-group">
              <label>
                <input
                  type="radio"
                  name="injuriesInvolved"
                  value="true"
                  checked={formData.injuriesInvolved === 'true'}
                  onChange={handleInputChange}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="injuriesInvolved"
                  value="false"
                  checked={formData.injuriesInvolved === 'false'}
                  onChange={handleInputChange}
                />
                No
              </label>
            </div>
          </div>

          {formData.injuriesInvolved === 'true' && (
            <div className="form-row">
              <div className="form-group">
                <label>Type of Injury *</label>
                <select name="injuryType" value={formData.injuryType} onChange={handleInputChange}>
                  <option value="">Select Injury Type</option>
                  <option value="Broken leg">Broken leg</option>
                  <option value="Head injury">Head injury</option>
                  <option value="Back injury">Back injury</option>
                  <option value="Multiple injuries">Multiple injuries</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div className="form-group">
                <label>Severity *</label>
                <select name="injurySeverity" value={formData.injurySeverity} onChange={handleInputChange}>
                  <option value="">Select Severity</option>
                  <option value="Minor">Minor</option>
                  <option value="Moderate">Moderate</option>
                  <option value="Severe">Severe</option>
                  <option value="Critical">Critical</option>
                </select>
              </div>
            </div>
          )}
        </div>

        {/* Claimant Information */}
        <div className="form-section">
          <h3>Claimant Information</h3>
          <div className="form-group">
            <label>Is Claimant the Policyholder? *</label>
            <div className="radio-group">
              <label>
                <input
                  type="radio"
                  name="isClaimantPolicyholder"
                  value="true"
                  checked={formData.isClaimantPolicyholder === 'true'}
                  onChange={handleInputChange}
                />
                Yes
              </label>
              <label>
                <input
                  type="radio"
                  name="isClaimantPolicyholder"
                  value="false"
                  checked={formData.isClaimantPolicyholder === 'false'}
                  onChange={handleInputChange}
                />
                No
              </label>
            </div>
          </div>

          {formData.isClaimantPolicyholder === 'false' && (
            <div className="form-row">
              <div className={`form-group ${errors.claimantName ? 'error' : ''}`}>
                <label>Claimant Name *</label>
                <input
                  type="text"
                  name="claimantName"
                  value={formData.claimantName}
                  onChange={handleInputChange}
                />
                {errors.claimantName && <div className="error-message">{errors.claimantName}</div>}
              </div>
              <div className={`form-group ${errors.relationship ? 'error' : ''}`}>
                <label>Relationship to Policyholder *</label>
                <select name="relationship" value={formData.relationship} onChange={handleInputChange}>
                  <option value="">Select Relationship</option>
                  <option value="SPOUSE">Spouse</option>
                  <option value="CHILD">Child</option>
                  <option value="PARENT">Parent</option>
                  <option value="SIBLING">Sibling</option>
                  <option value="OTHER">Other</option>
                </select>
                {errors.relationship && <div className="error-message">{errors.relationship}</div>}
              </div>
            </div>
          )}

          <div className="form-row">
            <div className={`form-group ${errors.phone ? 'error' : ''}`}>
              <label>Phone Number</label>
              <input
                type="tel"
                name="phone"
                value={formData.phone}
                onChange={handleInputChange}
                placeholder="+1234567890"
              />
              {errors.phone && <div className="error-message">{errors.phone}</div>}
            </div>
            <div className={`form-group ${errors.email ? 'error' : ''}`}>
              <label>Email Address</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="email@example.com"
              />
              {errors.email && <div className="error-message">{errors.email}</div>}
            </div>
          </div>

          <div className={`form-group ${errors.claimAmount ? 'error' : ''}`}>
            <label>Claim Amount *</label>
            <input
              type="number"
              name="claimAmount"
              value={formData.claimAmount}
              onChange={handleInputChange}
              min="0.01"
              step="0.01"
              placeholder="0.00"
            />
            {errors.claimAmount && <div className="error-message">{errors.claimAmount}</div>}
          </div>
        </div>

        {/* Declaration */}
        <div className="form-section">
          <div className={`form-group ${errors.declarationAccepted ? 'error' : ''}`}>
            <div className="checkbox-group">
              <input
                type="checkbox"
                name="declarationAccepted"
                checked={formData.declarationAccepted}
                onChange={handleInputChange}
              />
              <label>
                I declare that the information provided is true and accurate to the best of my knowledge *
              </label>
            </div>
            {errors.declarationAccepted && <div className="error-message">{errors.declarationAccepted}</div>}
          </div>
        </div>

        <button type="submit" className="submit-btn" disabled={loading}>
          {loading ? 'Submitting...' : 'Submit Claim'}
        </button>
      </form>

      {showOTPModal && (
        <OTPModal
          onVerify={handleOTPVerification}
          onClose={() => setShowOTPModal(false)}
          generatedOTP={generatedOTP}
        />
      )}
    </div>
  );
};

export default SubmitClaim;