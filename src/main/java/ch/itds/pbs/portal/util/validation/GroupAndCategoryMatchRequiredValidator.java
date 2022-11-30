package ch.itds.pbs.portal.util.validation;

import ch.itds.pbs.portal.domain.Category;
import ch.itds.pbs.portal.domain.MasterTile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GroupAndCategoryMatchRequiredValidator implements
        ConstraintValidator<GroupAndCategoryMatchRequired, Object> {

    private transient String message;

    @Override
    public void initialize(GroupAndCategoryMatchRequired constraint) {
        message = constraint.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        boolean groupIsValid = false;

        if (value instanceof MasterTile masterTile) {
            if (masterTile.getCategory() != null) {
                Category c = masterTile.getCategory();
                if (c.getMidataGroupOnly() == null) {
                    groupIsValid = true;
                } else if (masterTile.getMidataGroupOnly() != null) {
                    if (masterTile.getMidataGroupOnly().getId().equals(c.getMidataGroupOnly().getId())) {
                        groupIsValid = true;
                    }
                }
            }
        }

        if (!groupIsValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addPropertyNode("category").addConstraintViolation();
            return false;
        }
        return true;
    }
}
