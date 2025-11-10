export const validationRules = {
  policyNumber: {
    required: 'Policy number is required',
    pattern: {
      value: /^(POL\d{8}|INS-\d{9})$/,
      message: 'Invalid policy number format (POL12345678 or INS-123456789)'
    }
  },
  claimType: {
    required: 'Claim type is required'
  },
  natureOfClaim: {
    required: 'Nature of claim is required'
  },
  dateOfIncident: {
    required: 'Date of incident is required',
    validate: {
      notFuture: (value) => {
        const today = new Date();
        const incidentDate = new Date(value);
        return incidentDate <= today || 'Incident date cannot be in the future';
      }
    }
  },
  locationOfIncident: {
    required: 'Location of incident is required',
    pattern: {
      value: /^[a-zA-Z\s]+$/,
      message: 'Location must contain only alphabets and spaces'
    }
  },
  descriptionOfIncident: {
    required: 'Description of incident is required',
    minLength: {
      value: 15,
      message: 'Description must be at least 15 characters'
    },
    maxLength: {
      value: 500,
      message: 'Description must not exceed 500 characters'
    }
  },
  wasThirdPartyInvolved: {
    required: 'Please specify if third party was involved'
  },
  policeReportFiled: {
    required: 'Please specify if police report was filed'
  },
  isClaimantPolicyholder: {
    required: 'Please specify if claimant is the policyholder'
  },
  phone: {
    pattern: {
      value: /^\+?[1-9]\d{1,14}$/,
      message: 'Invalid phone number format'
    }
  },
  email: {
    pattern: {
      value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: 'Invalid email format'
    }
  },
  claimAmount: {
    required: 'Claim amount is required',
    min: {
      value: 0.01,
      message: 'Claim amount must be positive'
    }
  },
  declarationAccepted: {
    required: 'You must accept the declaration'
  },
  otp: {
    required: 'OTP is required',
    pattern: {
      value: /^\d{6}$/,
      message: 'OTP must be 6 digits'
    }
  }
};

export const validateField = (name, value, rules) => {
  const rule = rules[name];
  if (!rule) return null;

  // Handle null, undefined, and empty values
  const isEmpty = value === null || value === undefined || 
                  (typeof value === 'string' && value.trim() === '') ||
                  (typeof value === 'boolean' && value === false && name === 'declarationAccepted');

  if (rule.required && isEmpty) {
    return typeof rule.required === 'string' ? rule.required : `${name} is required`;
  }

  if (rule.pattern && value && !rule.pattern.value.test(value)) {
    return rule.pattern.message;
  }

  if (rule.minLength && value && value.length < rule.minLength.value) {
    return rule.minLength.message;
  }

  if (rule.maxLength && value && value.length > rule.maxLength.value) {
    return rule.maxLength.message;
  }

  if (rule.min && value && parseFloat(value) < rule.min.value) {
    return rule.min.message;
  }

  if (rule.validate && value !== null && value !== undefined) {
    try {
      for (const [key, validator] of Object.entries(rule.validate)) {
        if (typeof validator === 'function') {
          const result = validator(value);
          if (result !== true) {
            return result;
          }
        }
      }
    } catch (error) {
      console.error('Validation error:', error);
      return 'Validation failed';
    }
  }

  return null;
};