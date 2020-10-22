package ch.itds.pbs.portal.util.validation;

import ch.itds.pbs.portal.domain.Language;
import ch.itds.pbs.portal.domain.LocalizedString;
import org.apache.logging.log4j.util.Strings;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OneLocalizationRequiredValidator implements
        ConstraintValidator<OneLocalizationRequired, Object> {

    private static final SpelExpressionParser spelExprParser = new SpelExpressionParser();
    private transient String message;
    private transient String[] fields;

    @Override
    public void initialize(OneLocalizationRequired constraint) {
        message = constraint.message();
        fields = constraint.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        List<Language> completeLocalizations = new LinkedList<>(Arrays.asList(Language.values()));

        for (String fieldExpression : fields) {
            LocalizedString field = (LocalizedString) spelExprParser.parseExpression(fieldExpression).getValue(value);
            if (field == null) {
                return false;
            }
            if (Strings.isEmpty(field.getDe())) {
                completeLocalizations.remove(Language.DE);
            }
            if (Strings.isEmpty(field.getFr())) {
                completeLocalizations.remove(Language.FR);
            }
            if (Strings.isEmpty(field.getIt())) {
                completeLocalizations.remove(Language.IT);
            }
            if (Strings.isEmpty(field.getEn())) {
                completeLocalizations.remove(Language.EN);
            }
        }

        if (completeLocalizations.isEmpty()) {
            context.disableDefaultConstraintViolation();
            for (String fieldExpression : fields) {
                context.buildConstraintViolationWithTemplate(message).addPropertyNode(fieldExpression).addConstraintViolation();
            }
            return false;
        }
        return true;
    }
}
